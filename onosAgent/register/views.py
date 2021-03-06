from django.shortcuts import render

#from rest_framework.generics import GenericAPIView
import requests
from django.http import HttpResponse
import json
from django.http import JsonResponse

# Create your views here.
# return server health
#class ServerStatusViews(GenericAPIView):
#    def get(self, request, *args, **krgs):
def GetServerStatus(request):
    #return HttpResponse()
    return JsonResponse({'status':'ok'})
# start up registration to DMaaP topic
def PnfRegToDmp(request):
    getMsg = 1
    url = 'http://192.168.0.127:3904/events/unauthenticated.VES_PNFREG_OUTPUT/users/sdn-r?timeout=20000&limit=1'
    while getMsg:
        try:
            data = requests.get(url)
            d = data.json()
            j = json.loads(d[0])
            getMsg = 0
            userName = j['event']['pnfRegistrationFields']['additionalFields']['username']
            port = j['event']['pnfRegistrationFields']['additionalFields']['oamPort']
            serverIp = j['event']['pnfRegistrationFields']['oamV4IpAddress']
        except (IndexError,KeyError):
            getMsg = 1
    #        getMsg = 0
    #    except:
    #        getMsg = 1
    #status = RegToONOS(userName, port, serverIp)
    payload = '{"devices": {"netconf:' + serverIp + ':830' + '": {"netconf": {"ip": "' + serverIp + '","port": 830,"username":"' + userName + '","password": "netconf!"},"basic": {"driver": "ovs-netconf"}}}}'
    #send = payload.json()
    url = 'http://192.168.0.127:8181/onos/v1/network/configuration'
    r = requests.post(url, data=payload, auth=('onos', 'rocks'))

    return HttpResponse(r.status_code)
    #return JsonResponse({'userName':userName, 'port':port, 'serverIp':serverIp})

#def RegToONOS(username, port, ip):
    #"{\"devices\": {\"netconf:%s\": {\"netconf\": {\"ip\": \"%s\",\"port\": 830,\"username\":\"netconf\",\"password\": \"netconf!\"},\"basic\": {\"driver\": \"ovs-netconf\"}}}}"
#    payload = '{"devices": {"netconf:' + ip + ':830' + '": {"netconf": {"ip": "' + ip '","port": 830,"username":"' + username + '","password": "netconf!"},"basic": {"driver": "ovs-netconf"}}}}'
#    url = 'http://192.168.0.127:8181/onos/v1/network/configuration'
#    r = requests.post(url, data=payload)
#    return 200

def GetTest(request):
    getMsg = 1
    url = 'http://192.168.0.127:3904/events/My-Test-Topic/users/django?timeout=20000&limit=1000'
    data = requests.get(url)
    d = data.json()
    j = json.loads(d[0])
    #while getMsg:
    #    try:
    #        data = requests.get(url)
    #        d = data.json()
    #        j = json.loads(d)
    #        name = j["name"]
    #        getMsg = 0
    #    except:
    #        getMsg = 1
    #return JsonResponse({'name':name})
    #j = json.loads(data)
    name = j['name']
    return JsonResponse({'name':name})

# manually get topic message from DMaaP topic

# custom: register to specifi DMaaP topic
