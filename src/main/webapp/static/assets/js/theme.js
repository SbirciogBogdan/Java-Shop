// Custom theme code

if ($('.clean-gallery').length > 0) {
    baguetteBox.run('.clean-gallery', {animation: 'slideIn'});
}

if ($('.clean-product').length > 0) {
    $(window).on("load", function () {
        $('.sp-wrap').smoothproducts();
    });
}

// STUDENT CODE
const url_base = 'http://localhost:8888/';
const url_shopping_cart = url_base + 'cart.html';

//update quantity code (cart)
async function updateQuantity() {
    let prodQuantity = this.value;
    let prodId = this.getAttribute("data-prod-id");

    //prepare data
    let dataToBePosted = new FormData();
    dataToBePosted.append('quantity', prodQuantity);
    dataToBePosted.append( 'productId', prodId);
    dataToBePosted.append('operation', "updateQuantity");

    //send data
    await fetch(url_shopping_cart, {
        method: 'POST',
        body: dataToBePosted
    });

    //reload page
    window.location.reload();
}
let quantityInputs = document.getElementsByClassName("form-control quantity-input");
for (let quantityInput of quantityInputs) {
    quantityInput.addEventListener("click", updateQuantity);
}

//save cart code (cart)
async function saveCart() {
    //prepare data
    let dataToBePosted = new FormData();
    dataToBePosted.append('operation', "saveCart");

    //send data
    await fetch(url_shopping_cart, {
        method: 'POST',
        body: dataToBePosted
    });

    //reload
    window.location.reload();
}
let saveCartButton = document.getElementById("saveCart");
if(saveCartButton) {
    saveCartButton.addEventListener("click", saveCart);
}

//payment selection code (checkout)
let creditform = document.getElementById("creditform");
let paypalform = document.getElementById("paypalform");
let inputs;
function paymentSelect() {
    if(this.id === "creditRadio") {
        //show credit card form
        creditform.classList.remove("d-none");
        inputs = creditform.getElementsByTagName("input");
        for(let input of inputs) {
            input.setAttribute('required', true);
        }
        creditform.classList.add("d-block");

        //hide paypal form
        paypalform.classList.remove("d-block");
        inputs = paypalform.getElementsByTagName("input");
        for(let input of inputs) {
            input.removeAttribute('required');
        }
        paypalform.classList.add("d-none");
    } else {
        //hide credit card form
        creditform.classList.remove("d-block");
        inputs = creditform.getElementsByTagName("input");
        for(let input of inputs) {
            input.removeAttribute('required');
        }
        creditform.classList.add("d-none");

        //show paypal form
        paypalform.classList.remove("d-none");
        inputs = paypalform.getElementsByTagName("input");
        for(let input of inputs) {
            input.setAttribute('required', true);
        }
        paypalform.classList.add("d-block");
    }
}
let paymentSelectors = document.getElementsByName("paymentMethod");
for(let paymentSelector of paymentSelectors) {
    paymentSelector.addEventListener("click", paymentSelect);
}
