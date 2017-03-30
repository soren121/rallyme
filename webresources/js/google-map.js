function initMap() {
    var mapEl = document.querySelector('#google-map');
    // Show all of US in default view
    window.map = new google.maps.Map(mapEl, {
        center: {lat: 39.05, lng: -94.34},
        zoom: 5
    });

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
}

document.getElementById('location-search').addEventListener('submit', function(e) {
    // Disable normal form submission
    e.preventDefault();

    // Get search query from input box
    var searchQuery = e.target.elements[0].value.trim();
    if(searchQuery.length == 0) {
        return true;
    }

    // Lookup location using the Google Maps Geocoder API
    var geocoder = new google.maps.Geocoder();
    geocoder.geocode({address: searchQuery}, function(results, status) {
        if(status == 'OK') {
            // Center the map on the found location
            window.map.setCenter(results[0].geometry.location);
            // Zoom in
            window.map.setZoom(12);
        }
    });
});
