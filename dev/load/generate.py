from common import run_parallel, ACCOUNTS_COUNT, BALANCE
from client import create_client

POOL_SIZE = 10

def run_scenario(app_client, accounts_count, pool_size):
    step = accounts_count // pool_size
    factories = [(create_accounts, (app_client, BALANCE, i, i + step)) for i in range(0, accounts_count, step)]
    return run_parallel(*factories)

def create_accounts(app_client, balance, start, stop):
    for i in range(start, stop):
        account = app_client.create_account(balance)
        print('[%s] Account was created: %s' % (i, account))

if __name__ == '__main__':
    app_client = create_client()
    results = run_scenario(app_client, ACCOUNTS_COUNT, POOL_SIZE)
    for r in results:
        r.get()
    input("Press Enter to exit...")
    exit(0)
