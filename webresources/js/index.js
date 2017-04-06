function initMap() {
    var mapEl = document.querySelector('#google-map');
    // Show all of US in default view
    window.map = new google.maps.Map(mapEl, {
        center: {lat: 39.05, lng: -94.34}, // roughly the center of the US
        zoom: 5,
        mapTypeControl: false,
        streetViewControl: false,
        fullscreenControl: false
    });

    window.rallySlider = new RallySlider(document.getElementById("rally-drawer"));
    
    // call servlet Get for Json and place all markers on map
    $.getJSON("AjaxRally", function(data) {
        $.each(data, function(index, item) { 
            if(item.latitude !== null && item.longitude !== null) {
                var marker = new google.maps.Marker({
                    position: {
                        lat: item.latitude, 
                        lng: item.longitude
                    },
                    map: window.map,
                    animation: google.maps.Animation.DROP,
                    title: item.name
                });

                window.rallySlider.add(item);
            }
        });
    });
}

document.getElementById('location-search-submit').addEventListener('click', geocodeLookup);
document.getElementById('location-search-field').addEventListener('keyup', function(e) {
    if(event.keyCode == 13) geocodeLookup(e);
});

function geocodeLookup(e) {
    // Get search query from input box
    var searchQuery = document.getElementById('location-search-field').value.trim();
    if(searchQuery.length === 0) {
        return true;
    }

    // Lookup location using the Google Maps Geocoder API
    var geocoder = new google.maps.Geocoder();
    geocoder.geocode({address: searchQuery}, function(results, status) {
        if(status === 'OK') {
            // Center the map on the found location
            window.map.setCenter(results[0].geometry.location);
            // Zoom in
            window.map.setZoom(12);
        }
    });
}

document.getElementById('request-geolocation').addEventListener('click', function(e) {
    // Ask user for their location automatically
    if(navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(function(position) {
            // Set the map center to the user's location
            window.map.setCenter({
                lat: position.coords.latitude,
                lng: position.coords.longitude
            });

            // Zoom in
            window.map.setZoom(12);
        }, function() {
            alert('Geolocation failed :(');
        });
    }
});
