var seconds = 10;
var el = document.getElementById('seconds-counter');

function incrementSeconds() {
    seconds -= 1;
    if (seconds == 0) {
        location.replace('/shop');
    }
    el.innerText = `You will be redirected after ${seconds} seconds.`;
}

var cancel = setInterval(incrementSeconds, 1000);
