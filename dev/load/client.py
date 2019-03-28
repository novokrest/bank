from decimal import Decimal
import decimal
import requests as req
import itertools

APP_BASE_URL = 'http://localhost:18080/bank/api'

class AppClient:
    def __init__(self, base_url):
        self.base_url = base_url

    def create_account(self, balance):
        res = req.post(
            url = self.base_url + '/account/create',
            headers={
                'Content-type': 'application/json',
            },
            json={
                'balance': {
                    'amount': self._with_valid_precision(balance),
                    'currency': 'USD'
                }
            }
        )
        if 'account' not in res.json():
            raise Exception('Failed to create account')
        return res.json()['account']

    def transfer_money(self, src_account, dst_account, amount):
        res = req.post(
            url = self.base_url + '/transfer',
            headers={
                'Content-type': 'application/json',
            },
            json={
                'source': src_account,
                'destination': dst_account,
                'amount': {
                    'amount': self._with_valid_precision(amount),
                    'currency': 'USD'
                }
            }
        )
        if 'status' in res.json() and res.json()['status'] == 'Success':
            return True
        if 'errors' in res.json() and 'application' in res.json()['errors'] and 'code' in res.json()['errors']['application']:
            if res.json()['errors']['application']['code'] == 'RetryAfter':
                return False
        raise Exception('Failed to transfer: src={}, dst={}, amount={}, json={}'.format(src_account, dst_account, amount, res.json()))

    def get_balance(self, account):
        res = req.get(self.base_url + '/account/{}/balance'.format(account))
        if 'balance' not in res.json():
            raise Exception('Failed to get balance: account={}'.format(account))
        return int(res.json()['balance']['amount'])

    def _with_valid_precision(self, amount):
        return str(Decimal(amount).quantize(Decimal('.01'), rounding=decimal.ROUND_DOWN))

def create_client():
    return AppClient(base_url = APP_BASE_URL)
