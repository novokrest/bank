import random as R
from itertools import combinations
from common import run_parallel, ACCOUNTS_COUNT, BALANCE
from client import create_client

START_ACCOUNT = 1000000001
POOL_SIZE = 10

def run_scenario(app_client):
    accounts = range(START_ACCOUNT, START_ACCOUNT + ACCOUNTS_COUNT)
    account_pairs = [pair for pair in combinations(accounts, 2) if pair[0] != pair[1]]
    account_pairs = account_pairs + account_pairs[::-1]
    R.shuffle(account_pairs)
    step = len(account_pairs) // POOL_SIZE
    transfers = [(transfer, (app_client, account_pairs[i:i + step])) for i in range(0, len(account_pairs), step)]
    account_ops = {}
    total_fails_count = 0
    results = run_parallel(*transfers)
    for r in results:
        r_account_ops, fails_count = r.get()
        total_fails_count += fails_count
        for account, ops in r_account_ops.items():
            for amount in ops:
                add_account_ops(account_ops, account, amount)
    check_balances(app_client, account_ops)
    print('Total fails: %s' % total_fails_count)

def transfer(app_client, account_pairs):
    account_ops = { }
    fails_count = 0
    for pair in account_pairs:
        k = 1 if R.random() < 0.5 else -1
        amount_to_transfer = R.randint(10, 30)
        src_account, dst_account = pair
        success = app_client.transfer_money(src_account, dst_account, amount_to_transfer)
        if success:
            add_account_ops(account_ops, src_account, -amount_to_transfer)
            add_account_ops(account_ops, dst_account, amount_to_transfer)
        else:
            fails_count += 1
    return account_ops, fails_count

def add_account_ops(account_ops, account, amount):
    if account not in account_ops:
        account_ops[account] = []
    account_ops[account].append(amount)

def check_balances(app_client, account_ops):
    for account, ops in account_ops.items():
        expected_balance = BALANCE + sum(ops)
        actual_balance = app_client.get_balance(account)
        if expected_balance != actual_balance:
            raise Exception('Incorrect balance: account={}, expected={}, actual={}, ops={}'.format(account, expected_balance, actual_balance, ops))
        print('Account was checked successfully: account={}'.format(account))

if __name__ == '__main__':
    app_client = create_client()
    run_scenario(app_client)
