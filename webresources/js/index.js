/**
    RallyMe
    CSCI 4300, CRN 41126, Group 5

    index.js
 */

'use strict';

/**
 * Initializes the page.
 */
function initMap() {
    // roughly the center of the US
    var userLatitude = 39.05, userLongitude = -94.34; 
    var userLocationIsSet = false;
    // If localStorage is available, load map with saved location if we have it
    if(storageAvailable('localStorage')) {
        if(localStorage.getItem('latitude') !== null && localStorage.getItem('longitude') !== null) {
            userLatitude = parseFloat(localStorage.latitude);
            userLongitude = parseFloat(localStorage.longitude);
            userLocationIsSet = true;
            document.getElementById("location-prompt").style.display = "none";
            document.getElementById("facebook-button").style.display = "inline-block";
        }
    }

    // Get map
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
    // Initialize the rally slider
    window.rallySlider = new RallySlider(document.getElementById("rally-drawer"));

    var ajaxOptions = {
        latitude: userLatitude, 
        longitude: userLongitude, 
        radius: 25, 
        source: "database"
    };
    
    // Load rallies
    loadRallies(ajaxOptions, true).done(loadFromURL);
}

/**
 * Loads rallies from the AjaxRally controller.
 * @param {object} ajaxOptions - An object of parameters to pass to AjaxRally
 * @param {boolean} clear - True if the list pane should be cleared before loading.
 */
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

    // Request rally JSON from AjaxRally and load rallies into list pane
    return $.getJSON("AjaxRally", ajaxOptions).done(function(data) {
        $.each(data, function(index, item) {
            // For each rally, if we have valid coordinates, create a map marker
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

                // Add marker to map
                window.mapMarkers.push(marker);

                // If we're looking up a single rally (i.e. when loading 
                // a non-local sister rally), we should only save its data.
                // Otherwise, render an item in the list pane as well.
                if(ajaxOptions.hasOwnProperty('rally_id')) {
                    window.rallySlider.items[item.id] = item;
                } else {
                    window.rallySlider.add(item.type.toLowerCase(), item);
                }
            }
        });

        // Add empty message if no local rallies were found
        if(window.rallySlider.lists.local.childNodes.length === 0) {
            window.rallySlider.lists.local.textContent = "We couldn't find any rallies in your area.";
        }
    });
}

// Define event listeners
// loadFromURL should be called when page is loaded or manual navigation occurs, to update displayed rally
window.addEventListener("popstate", loadFromURL);
// geocodeLookup should be called when the location search form is submitted
// We use the click and keyup events to imitate default form behavior
document.getElementById('location-search-submit').addEventListener('click', geocodeLookup);
document.getElementById('location-search-field').addEventListener('keyup', function(e) {
    // Only lookup when Enter key (key 13) is pressed
    if(e.keyCode == 13) geocodeLookup(e);
});

/**
 * Loads the rally specified in the current URL, or destroys the detail pane if
 * no rally is to be loaded.
 */
function loadFromURL() {
    var currentUrl = window.location.pathname;
    var deeplinkRe = /\/Rally\/\d+$/;

    // Test if current URL is a rally deeplink
    if(deeplinkRe.test(currentUrl)){
        // Grab the ID from the end of the URL and display the detail pane
        var urlarray = currentUrl.split('/');
        window.rallySlider.showDetailPane(parseInt(urlarray[urlarray.length - 1]), true);
    } else {
        // If no rally is specified, destroy the detail pane if it's shown
        window.rallySlider.destroyDetailPane();
    }
}

/**
 * Looks up your geocoordinates given a physical location and displays local rallies near you.
 */
function geocodeLookup() {
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

            // Load local rallies near found location
            loadRallies(ajaxOptions, true);
            document.getElementById("facebook-button").style.display = "inline-block";
        }
    });
}

/**
 * Use the HTML5 Geolocation API to get the user's current location and 
 * display local rallies near them.
 */
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

            // Load rallies near found location
            loadRallies(ajaxOptions, true);
            e.target.parentNode.style.display = "none";
        }, function() {
            alert('Geolocation failed :(');
        });
    }
});

/**
 * Load rallies from Facebook when the "Find rallies on FB" button is clicked.
 */
document.getElementById('facebook-button').addEventListener('click', function(e) {
    var mapCenter = window.map.getCenter();

    // Get location from map
    var ajaxOptions = {
        latitude: mapCenter.lat, 
        longitude: mapCenter.lng, 
        radius: 75,
        source: "facebook"
    };

    // Disable button while FacebookEventSearch does its thing
    e.target.disabled = true;
    var origContent = e.target.innerHTML;
    e.target.textContent = "Loading...";
    loadRallies(ajaxOptions, false).done(function() {
        // Reactivate button
        e.target.innerHTML = origContent;
        e.target.disabled = false;
    });
});

/**
 * Verifies that the HTML5 Storage API is available and enabled.
 * @param {string} type - localStorage or sessionStorage.
 * @return {boolean} Status of the given storage API.
 */
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
