from apscheduler.schedulers.background import BackgroundScheduler
from .jobs import get_new_data
import atexit


def start():
    """
    adds job to retrieve data from databricks to scheduler
    :return:
    """
    scheduler = BackgroundScheduler()
    scheduler.add_job(get_new_data, 'interval', days=1000)
    scheduler.start()
    # Shut down the scheduler when exiting the app
    atexit.register(lambda: scheduler.shutdown())
