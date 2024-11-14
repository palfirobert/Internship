#!/usr/bin/env python
__package__ = None

"""Django's command-line utility for administrative tasks."""
import os
import sys

def main():

    """Run administrative tasks."""
    os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'PlotService.settings')
    try:
        from django.core.management import execute_from_command_line
    except ImportError as exc:
        raise ImportError(
            "Couldn't import Django. Are you sure it's installed and "
            "available on your PYTHONPATH environment variable? Did you "
            "forget to activate a virtual environment?"
        ) from exc
    execute_from_command_line(sys.argv)


def ready():
    from Jobs import updater
    updater.start()


if __name__ == '__main__':
    #ready()
    main()


