"""
URL configuration for airpeekbackend project.

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/4.2/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  path('', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  path('', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.urls import include, path
    2. Add a URL to urlpatterns:  path('blog/', include('blog.urls'))
"""
from django.contrib import admin
from django.urls import path

from airpeekbackendapp import endpoints

urlpatterns = [
    path('admin/', admin.site.urls),
    path('user/flights', endpoints.user_flights),
    path('user/flights/<str:flight>', endpoints.user_flightsdetail),
    path('user', endpoints.user),
    path('user/session', endpoints.sessions),
    path('special_offers', endpoints.special_offers),
    path('flights', endpoints.flights),
]
