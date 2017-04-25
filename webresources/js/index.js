// Check to see if local or session storage is available
function storageAvailable(type) {
	try {
		var storage = window[type], x = '__storage_test__';
		storage.setItem(x, x);
		storage.removeItem(x);
		return true;
	} catch(e) {
		return false;
	}
}

function loadRallies(ajaxOptions, clear) {
    if(clear === true) {
        // Clear lists
        $("#national-rallies, #local-rallies").empty();

        // Clear existing map markers
        for(var i = 0; i < window.mapMarkers.length; i++) {
            window.mapMarkers[i].setMap(null);
        }
        window.mapMarkers.length = 0;
    }

    // call servlet Get for Json and place all markers on map
    return $.getJSON("AjaxRally", ajaxOptions).done(function(data) {
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

                window.mapMarkers.push(marker);
                window.rallySlider.add(item.type.toLowerCase(), item);
            }
        });

        // Add empty message if no local rallies were found
        if(window.rallySlider.lists.local.childNodes.length === 0) {
            window.rallySlider.lists.local.textContent = "We couldn't find any rallies found in your area.";
        }
    });
}

function initMap() {
    // roughly the center of the US
    var userLatitude = 39.05, userLongitude = -94.34; 
    var userLocationIsSet = false;
    if(storageAvailable('localStorage')) {
        if(localStorage.getItem('latitude') !== null && localStorage.getItem('longitude') !== null) {
            userLatitude = parseFloat(localStorage.latitude);
            userLongitude = parseFloat(localStorage.longitude);
            userLocationIsSet = true;
            document.getElementById("location-prompt").style.display = "none";
            document.getElementById("facebook-button").style.display = "inline-block";
        }
    }

    var mapEl = document.querySelector('#google-map');
    // Show all of US in default view
    window.map = new google.maps.Map(mapEl, {
        center: {lat: userLatitude, lng: userLongitude}, 
        zoom: userLocationIsSet ? 12 : 5,
        mapTypeControl: false,
        streetViewControl: false,
        fullscreenControl: false
    });

    window.mapMarkers = [];
    window.rallySlider = new RallySlider(document.getElementById("rally-drawer"));

    var ajaxOptions = {
        latitude: userLatitude, 
        longitude: userLongitude, 
        radius: 25, 
        source: "database"
    };
    
    loadRallies(ajaxOptions, true).done(loadFromURL);	
 
} //end of initMap

document.getElementById('location-search-submit').addEventListener('click', geocodeLookup);
document.getElementById('location-search-field').addEventListener('keyup', function(e) {
    if(e.keyCode == 13) geocodeLookup(e);
});


function loadFromURL(e){
	var currentUrl = window.location.pathname;
    var deeplinkRe = /\/Rally\/\d+$/;
    if(deeplinkRe.test(currentUrl)){
    	 var urlarray = currentUrl.split('/');
    	 window.rallySlider.showDetailPane(parseInt(urlarray[urlarray.length - 1]), true);
    } else {
    	window.rallySlider.destroyDetailPane();
    }
}

window.addEventListener("popstate", loadFromURL);

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

            var ajaxOptions = {
                latitude: results[0].geometry.location.lat(), 
                longitude: results[0].geometry.location.lng(), 
                radius: 25,
                source: "database"
            };

            loadRallies(ajaxOptions, true);
            document.getElementById("facebook-button").style.display = "inline-block";
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

            // Save location in local storage
            if(storageAvailable('localStorage')) {
                localStorage.latitude = position.coords.latitude;
                localStorage.longitude = position.coords.longitude;
                document.getElementById("facebook-button").style.display = "inline-block";
            }

            var ajaxOptions = {
                latitude: position.coords.latitude, 
                longitude: position.coords.longitude, 
                radius: 25,
                source: "database"
            };

            loadRallies(ajaxOptions, true);
            e.target.parentNode.style.display = "none";
        }, function() {
            alert('Geolocation failed :(');
        });
    }
});

document.getElementById('facebook-button').addEventListener('click', function(e) {
    var mapCenter = window.map.getCenter();

    var ajaxOptions = {
        latitude: mapCenter.lat, 
        longitude: mapCenter.lng, 
        radius: 75,
        source: "facebook"
    };

    e.target.disabled = true;
    var origContent = e.target.innerHTML;
    e.target.textContent = "Loading...";
    loadRallies(ajaxOptions, false).done(function() {
        e.target.innerHTML = origContent;
        e.target.disabled = false;
    });
});
