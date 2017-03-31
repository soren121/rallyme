function initMap() {
    var mapEl = document.querySelector('#google-map');
    // Show all of US in default view
    window.map = new google.maps.Map(mapEl, {
        center: {lat: 39.05, lng: -94.34},
        zoom: 5,
        mapTypeControl: false,
        streetViewControl: false,
        fullscreenControl: false
    });
    
    //call servlet Get for Json and place all markers on map
    var latitude;
    var longitude;
    var title;
    
	$.getJSON( "AjaxRally", function( data ) {
    	  $.each( data, function(index, item) { 
    		  latitude = null;
    		  longitude = null;
    		  title = null;
			$.each(item, function(key, value){
				if(key == "latitude"){
					latitude = value;
				} else if(key == "longitude") {
					longitude = value;
				} else if(key == "name"){
					title = value;
				} 			
			});
			
			if(latitude != null && longitude != null && title != null){
				var myLatLng = {lat: latitude, lng: longitude};
				var marker = new google.maps.Marker({
			        position: myLatLng,
			        map: map,
			        animation: google.maps.Animation.DROP,
			        title: title
			      });
			  }
    	  });
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
