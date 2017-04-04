'use strict';

function RallySlider(element) {
    this.element = element;
    this.toggleEle = element.querySelector(".drawer-toggle");
    this.list = element.querySelector("ul");
    this.detailPaneEle = element.querySelector("#rally-detail");

    this.items = {};
    this.detailPaneId = -1;

    this.toggleEle.addEventListener("click", function(e) {
        e.target.parentNode.classList.toggle("closed");
    });
}

RallySlider.prototype.toggle = function() {
    this.element.classList.toggle("closed");
};

RallySlider.prototype.add = function(item) {
    this.items[item.id] = item;

    var pagefn = doT.template(document.getElementById('rally-list-tpl').text);
    var data = {
        id: item.id,
        url: 'javascript:window.rallySlider.showDetailPane(' + item.id + ');',
        avatar: (item.twitterHandle !== null && item.twitterHandle.length > 0) ? 
            'https://twitter.com/' + item.twitterHandle + '/profile_image' : '',
        name: item.name
    };

    this.list.insertAdjacentHTML('beforeend', pagefn(data));
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
        alert('detail pane already exists!');
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
        organizerName: item.creator.firstName + ' ' + item.creator.lastName
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
    window.map.setZoom(16);

    // Load Twitter timeline
    twttr.widgets.load(this.detailPaneEle);
};
