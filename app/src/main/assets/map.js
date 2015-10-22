var map;
var directionsService = new google.maps.DirectionsService();

var start, end, waypoints;
var currentPosition, currentMarker;



function initialize()
{
    directionsDisplay = new google.maps.DirectionsRenderer();
    var mapOptions =
    {
        zoom: 8,
        mapTypeId: google.maps.MapTypeId.ROADMAP,
        center: new google.maps.LatLng(23.5, 121.0)
    };
    map = new google.maps.Map(document.getElementById("map_canvas"), mapOptions);
    directionsDisplay.setMap(map);
}



function markMe(lat,log)
{   
    if(currentMarker!=null) currentMarker.setMap(null);

    currentPosition = new google.maps.LatLng(lat,log);
    currentMarker = new google.maps.Marker({
        map: map,
        position: currentPosition,
        icon: 'gary.png'
    });
    map.setCenter(currentPosition);
    map.setZoom(15);
}


//丟點就會生成路徑
function calcRoute(input)
{
    if (!input) return;
    

    // passed ppints
    var arrPoints = input.split(",");
    var wayPoints = [];
    for (var i=0; i < arrPoints.length-1; i++)
        wayPoints.push({location: arrPoints[i], stopover: true});
    end = arrPoints[arrPoints.length-1];


    // Request of route planning
    var request =
    {
        origin: currentPosition,
        destination: end,
        waypoints: wayPoints,
        optimizeWaypoints: true,
        travelMode: google.maps.DirectionsTravelMode.DRIVING
    };
    

    // Plot the calculated route
    directionsService.route(request, function(response, status)
    {
        // Return the result of calculated route
        if (status == google.maps.DirectionsStatus.OK)
        {
            directionsDisplay.setOptions({ preserveViewport: true });
            directionsDisplay.setDirections(response);
            map.panTo(start);
        }
    });
}



function centerAt(lat,log)
{
    map.panTo(new google.maps.LatLng(lat,log));
}    


