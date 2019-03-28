from multiprocessing import Pool

ACCOUNTS_COUNT = 50
BALANCE = 1000000

def run_parallel(*fns):
    pool = Pool(processes=len(fns))
    return [pool.apply_async(fn, args) for fn, args in fns]
