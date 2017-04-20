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

RallySlider.prototype.add = function(type, item) {
    this.items[item.id] = item;

    var pagefn = doT.template(document.getElementById('rally-list-tpl').text);
    var data = {
        id: item.id,
        url: 'javascript:window.rallySlider.showDetailPane(' + item.id + ');',
        avatar: (item.twitterHandle !== null && item.twitterHandle.length > 0) ? 
            'https://twitter.com/' + item.twitterHandle + '/profile_image' : '',
        name: item.name
    };

    this.lists[type].insertAdjacentHTML('beforeend', pagefn(data));
};

RallySlider.prototype.destroyDetailPane = function() {
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
};

RallySlider.prototype.showDetailPane = function(id) {
    if(this.detailPaneId !== -1) {
        this.destroyDetailPane();   
        setTimeout( function() {
        	this.showDetailPane(id);
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
            'https://twitter.com/' + item.twitterHandle + '/profile_image?size=original' : '',
        name: item.name,
        description: item.description,
        location: item.location,
        startTime: item.startTime,
        capacity: item.eventCapacity,
        organizerName: item.creator.firstName + ' ' + item.creator.lastName,
        parentRally: 'javascript:window.rallySlider.showDetailPane(' + item.parent_id + ');',
        parentRallyId: item.parent_id,
        parentRallyName: item.parent_name,
        sisterRalliesId: 'javascript:window.rallySlider.showDetailPane(' + item.sister_rallies_id + ');',
        sisterRalliesName: item.sister_rallies_name,
        sisterRalliesJavascript: item.javascript_sister_rallies,
        numbersisterRallies: item.number_of_sister_rallies
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
};
