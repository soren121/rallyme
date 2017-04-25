'use strict';
function RallySlider(element) {
    this.element = element;
    this.toggleEle = element.querySelector(".drawer-toggle");
    this.detailPaneEle = element.querySelector("#rally-detail");
    this.lists = {
        national: element.querySelector("#national-rallies"),
        local: element.querySelector("#local-rallies")
    };

    this.items = {};
    this.detailPaneId = -1;

    this.toggleEle.addEventListener("click", function(e) {
        e.target.parentNode.classList.toggle("closed");
    });
}

RallySlider.prototype.toggle = function() {
    this.element.classList.toggle("closed");
};

RallySlider.prototype._parsePathname = function(appendStr) {
    var pathRe = /^(\/.+\/)(?=Rally\/\d+)/;
    var pathTest = pathRe.exec(window.location.pathname);
    var newPath = (pathTest !== null) ? pathTest[1] : window.location.pathname;

    if(newPath.slice(-1) !== '/') {
        newPath += '/';
    }

    return newPath + (appendStr || '');
};

RallySlider.prototype.add = function(type, item) {
    this.items[item.id] = item;

    var pagefn = doT.template(document.getElementById('rally-list-tpl').text);
    var data = {
        id: item.id,
        avatar: (item.twitterHandle !== null && item.twitterHandle.length > 0) ? 
            'https://twitter.com/' + item.twitterHandle + '/profile_image' : 
            'images/generic-rally.png',
        name: item.name
    };

    this.lists[type].insertAdjacentHTML('beforeend', pagefn(data));
};

RallySlider.prototype.destroyDetailPane = function(noPush) {
    if(this.detailPaneId === -1) {
        return;
    }

    this.element.classList.add("hold-open");
    this.detailPaneEle.classList.add("closed");
    setTimeout(function() {
        this.detailPaneId = -1;
        this.element.querySelector(".drawer-container").style.display = "block";
        $(this.detailPaneEle.querySelector(".drawer-container")).empty();
        this.element.classList.remove("hold-open");
    }.bind(this), 200);
    
    if(noPush !== true && this._parsePathname() !== window.location.pathname) {
        var stateObj = { state: "okey" };
        history.pushState(stateObj, "page 2", this._parsePathname());
    }
};

RallySlider.prototype.showDetailPane = function(id, noPush) {
    if(this.detailPaneId !== -1) {
        this.destroyDetailPane(true);   
        setTimeout( function() {
        	this.showDetailPane(id, noPush);
        }.bind(this), 200);      
        return;
    }
    
    var item = this.items[id];
    if(item === null || item === undefined) {
        alert('item invalid!');
        return;
    }

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
        organizerName: item.creator.firstName + ' ' + item.creator.lastName,
        parent: item.parent || null,
        sisters: item.sisters || []
    };

    this.detailPaneEle.querySelector(".drawer-container").innerHTML = pagefn(data);
    this.element.classList.add("hold-open");
    this.element.querySelector(".drawer-container").style.display = "none";
    this.detailPaneEle.classList.remove("closed");

    setTimeout(function() {
        this.element.classList.remove("hold-open");
    }.bind(this), 200);
    
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
    
    if(noPush !== true) {
        var stateObj = { state: "okey" };
        history.pushState(stateObj, "page 2", this._parsePathname("Rally/" + id));
    }
};
