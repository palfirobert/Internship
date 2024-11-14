"""
URL configuration for PlotService project.

The `urlpatterns` list routes URLs to views. Views are the functions called when a request comes
here  as an example the /hello url is linked to the view.hello function

"""
from django.urls import path
from . import views
from rest_framework_simplejwt import views as jwt_views

urlpatterns = [
    path('hello', views.hello),
    path('plot', views.show_plot),
    path('authenticate', jwt_views.TokenObtainPairView.as_view())
]
