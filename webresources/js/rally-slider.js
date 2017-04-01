'use strict';

function RallySlider(element) {
    this.element = element;
    this.toggleEle = element.querySelector("#rally-list-toggle");
    this.list = element.querySelector("ul");

    this.toggleEle.addEventListener("click", function(e) {
        e.target.parentNode.classList.toggle("closed");
    });
}

RallySlider.prototype.toggle = function() {
    this.element.classList.toggle("closed");
};

RallySlider.prototype.add = function(title) {
    var listItem = document.createElement("li");
    var listItemLink = document.createElement("a");
    listItemLink.href = "#";
    listItemLink.textContent = title;

    listItem.appendChild(listItemLink);
    this.list.appendChild(listItem);
};
