from django.http import JsonResponse
from rest_framework.decorators import api_view, authentication_classes, permission_classes
import json
from rest_framework.authentication import TokenAuthentication
from rest_framework.permissions import IsAuthenticated
from rest_framework.response import Response
from rest_framework_simplejwt.authentication import JWTAuthentication

from PythonApp.Domain.domain import PlotType
from PythonApp.Utils.ServiceFactory import ServiceFactory


@api_view(['GET'])
@authentication_classes([JWTAuthentication])
@permission_classes([IsAuthenticated])
def hello(request):
    hello_message = [
        {
            'Endpoint': '/hello',
            'method': 'POST',
            'body': 'None',
            'description': 'hello sper'
        }
    ]
    return JsonResponse(hello_message, safe=False)


"""
    STRUCTURE OF A REQUEST:

{
  "TenantId": "1aa9d5e8-a182-414b-be97-1791fd89fc27",
  "BatteryIds": ["0ENPE011011000B8B0000030","0ENPE011011000B8S0000019","asdadad"],
  "PlotType": "sohc"
}

"""


# tenantId/batteryId/sohc/hfahjhadjvhasj.PNG
@api_view(['POST'])
@authentication_classes([JWTAuthentication])
@permission_classes([IsAuthenticated])
def show_plot(request):
    try:
        # get data from request body
        data = json.loads(request.body)
        tenant_id = data.get('tenantId', None)
        battery_ids = data.get('batteryIds', None)
        print(data.get('plotType', None).upper())
        print(data.keys())
        plot_type = PlotType[data.get('plotType', None).upper()]
        print(tenant_id)
        print(battery_ids)
        print(plot_type)
        # initialize service
        service = ServiceFactory.initialise()

        # get the locations of the created plots
        locations = service.plot(tenant_id, battery_ids, plot_type)

        # Return a response with the processed data.
        response_data = {
            'message': 'Data received successfully',
            'locations': locations
        }

        return JsonResponse(response_data)

    except json.JSONDecodeError:
        # Return an error response if the JSON data is not valid.
        error_response = {
            'error': 'Invalid JSON data in the request body',
        }
        return JsonResponse(error_response, status=400)

