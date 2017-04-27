/**
    RallyMe
    CSCI 4300, CRN 41126, Group 5

    rally-slider.js
 */

'use strict';

/**
 * Initializes a rally slider pane.
 * @constructor
 * @param {HTMLElement} element - The root element of the RallySlider pane.
 */
function RallySlider(element) {
    // Set the elements we'll be accessing often
    this.element = element;
    this.toggleEle = element.querySelector(".drawer-toggle");
    this.detailPaneEle = element.querySelector("#rally-detail");
    this.lists = {
        national: element.querySelector("#national-rallies"),
        local: element.querySelector("#local-rallies")
    };

    // Create an object to hold all rally data
    this.items = {};
    // Initialize the ID that will be shown by the detail pane
    this.detailPaneId = -1;

    // Open/close the pane when the collapse gutter is clicked
    this.toggleEle.addEventListener("click", function(e) {
        e.target.parentNode.classList.toggle("closed");
    });
}

/**
 * Toggles the rally slider open or closed.
 */
RallySlider.prototype.toggle = function() {
    this.element.classList.toggle("closed");
};

/**
 * Parses the current URL to respect the deployed path of the application.
 * This function ensures that the URL remains correct when altering it via 
 * the Location API.
 * 
 * @param {string} appendStr - A path to append to the parsed pathname.
 * @return {string} The parsed pathname, with appendStr concatenated if passed.
 */
RallySlider.prototype._parsePathname = function(appendStr) {
    var pathRe = /^(\/.+\/)(?=Rally\/\d+)/;

    // Detect the path of the application, without deeplink paths
    var pathTest = pathRe.exec(window.location.pathname);
    // If the application is not deployed at the webroot, extract the path
    var newPath = (pathTest !== null) ? pathTest[1] : window.location.pathname;

    // Ensure that the returned pathname ends in a trailing slash
    if(newPath.slice(-1) !== '/') {
        newPath += '/';
    }

    // Return pathname, with appendStr if desired
    return newPath + (appendStr || '');
};

/**
 * Adds a rally to the RallySlider.
 * @param {string} 'local' or 'national'.
 * @param {object} A Rally object. Refer to the Rally class for its structure.
 */
RallySlider.prototype.add = function(type, item) {
    // Save rally object data
    this.items[item.id] = item;

    // Create template variables
    var pagefn = doT.template(document.getElementById('rally-list-tpl').text);
    var data = {
        id: item.id,
        avatar: (item.twitterHandle !== null && item.twitterHandle.length > 0) ? 
            'https://twitter.com/' + item.twitterHandle + '/profile_image' : 
            'images/generic-rally.png',
        name: item.name
    };

    // Render template and append to the specified list
    this.lists[type].insertAdjacentHTML('beforeend', pagefn(data));
};

/**
 * Destroys the detail pane (if shown) and returns to the rally list.
 * No action will occur if a detail pane does not currently exist.
 * @param {boolean} noPush - If true, do not alter the history state.
 */
RallySlider.prototype.destroyDetailPane = function(noPush) {
    // Exit if no detail pane exists
    if(this.detailPaneId === -1) {
        return;
    }

    // Hold the rally slider open while the detail pane is collapsed
    this.element.classList.add("hold-open");
    this.detailPaneEle.classList.add("closed");
    // Wait for the collapse animation to complete
    setTimeout(function() {
        // Reset detail pane ID to indicate that it's been destroyed
        this.detailPaneId = -1;
        // Reset the detail pane DOM
        this.element.querySelector(".drawer-container").style.display = "block";
        $(this.detailPaneEle.querySelector(".drawer-container")).empty();
        this.element.classList.remove("hold-open");
    }.bind(this), 200);
    
    // Alter the history state to indicate that we're at the app root now
    if(noPush !== true && this._parsePathname() !== window.location.pathname) {
        var stateObj = { state: "okey" };
        history.pushState(stateObj, "page 2", this._parsePathname());
    }
};

/**
 * Show the detail pane for the specified rally ID.
 * @param {int} id - The ID of the rally to show.
 * @param {boolean} noPush - If true, do not alter the history state.
 */
RallySlider.prototype.showDetailPane = function(id, noPush) {
    // If a detail pane already exists, collapse it and then recall 
    // this function when the previous detail pane has been destroyed
    if(this.detailPaneId !== -1) {
        this.destroyDetailPane(true);   
        setTimeout(function() {
        	this.showDetailPane(id, noPush);
        }.bind(this), 200);      
        return;
    }
    
    // Retrieve the rally data
    var item = this.items[id];
    // Create a Deferred object in case we need to wait on new data
    var defer = $.Deferred();

    // If the rally ID does not exist in the data object, make another call
    // to the server to retrieve it
    if(item === null || item === undefined) {
        // Load rally, resolve promise when complete
        loadRallies({rally_id: id, source: "database"}, false).done(function() {
            defer.resolve();
        });
    } else {
        // Rally data is already loaded, complete promise immediately
        defer.resolve();
    }

    // Wait for above promise to complete
    $.when(defer.promise()).done(function() {
        // Load rally data again
        item = this.items[id];
        // If it's still null, then the rally doesn't exist in the database either
        // Too bad :(
        if(item === null || item === undefined) {
            alert("The specified rally ID could not be loaded.");
            return;
        }

        // Render detail template
        var pagefn = doT.template(document.getElementById('rally-detail-tpl').text);
        var data = {
            id: item.id,
            url: item.url,
            twitterHandle: item.twitterHandle,
            avatar: (item.twitterHandle !== null && item.twitterHandle.length > 0) ? 
                'https://twitter.com/' + item.twitterHandle + '/profile_image?size=original' : 
                'images/generic-rally.png',
            name: item.name,
            description: item.description,
            location: item.location,
            startTime: item.startTime,
            capacity: item.eventCapacity,
            organizerName: (item.creator !== null) ? 
                item.creator.firstName + ' ' + item.creator.lastName : 
                'Imported from Facebook',
            parent: item.parent || null,
            sisters: item.sisters || []
        };

        // Open detail pane, hold open while list element is collapsed
        this.detailPaneEle.querySelector(".drawer-container").innerHTML = pagefn(data);
        this.element.classList.add("hold-open");
        this.element.querySelector(".drawer-container").style.display = "none";
        this.detailPaneEle.classList.remove("closed");

        // Remove hold when animation is complete
        setTimeout(function() {
            this.element.classList.remove("hold-open");
        }.bind(this), 200);
        
        // Set ID of the currently displayed rally
        this.detailPaneId = id;

        // Set map center
        window.map.setCenter({
            lat: item.latitude,
            lng: item.longitude
        });

        // Zoom in
        window.map.setZoom(15);

        // Load Twitter timeline
        twttr.widgets.load(this.detailPaneEle);
        
        // Alter history state to include deeplink
        if(noPush !== true) {
            var stateObj = { state: "okey" };
            history.pushState(stateObj, "page 2", this._parsePathname("Rally/" + id));
        }
    }.bind(this));
};
