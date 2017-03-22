function initMap() {
    var mapEl = document.querySelector('#google-map');
    // Show all of US in default view
    var map = new google.maps.Map(mapEl, {
        center: {lat: 39.05, lng: -94.34},
        zoom: 5
    });

    // Ask user for their location automatically
    if(navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(function(position) {
            // Set the map center to the user's location
            map.setCenter({
                lat: position.coords.latitude,
                lng: position.coords.longitude
            });

            // Zoom in
            map.setZoom(12);
        }, function() {
            alert('Geolocation failed :(');
        });
    }
}
