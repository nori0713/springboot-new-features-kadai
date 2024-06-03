const stripe = Stripe('pk_test_51PNWvZP0vfuEMgGe35EFrxH8HOPJdxIccbdxYnzCcH86PtANs8NhTt3TmVSvhOaS2iKumUxj9jbnQg5hg1C83sJb00kAwIMReF');
 const paymentButton = document.querySelector('#paymentButton');
 
 paymentButton.addEventListener('click', () => {
   stripe.redirectToCheckout({
     sessionId: sessionId
   })
 });