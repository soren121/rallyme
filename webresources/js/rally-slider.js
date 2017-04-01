'use strict';

function RallySlider(element) {
    this.element = element;
    this.toggleEle = element.querySelector(".drawer-toggle");
    this.list = element.querySelector("ul");

    this.toggleEle.addEventListener("click", function(e) {
        e.target.parentNode.classList.toggle("closed");
    });
}

RallySlider.prototype.toggle = function() {
    this.element.classList.toggle("closed");
};

RallySlider.prototype.add = function(id, title, handle) {
    var pagefn = doT.template(document.getElementById('rally-list-tpl').text);
    var data = {
        id: id,
        url: '#',
        avatar: (handle !== null && handle.trim().length > 0) ? 'https://twitter.com/' + handle + '/profile_image' : '',
        name: title
    };

    this.list.insertAdjacentHTML('beforeend', pagefn(data));
};
