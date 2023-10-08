import {useEffect, useState} from 'react';

function Completion(props : any) {
  const [ messageBody, setMessageBody ] = useState<string | JSX.Element>("");
  const { stripePromise } = props;

  useEffect(() => {
    if (!stripePromise) return;

    stripePromise.then(async (stripe : any) => {
      const clientSecret = new URLSearchParams(window.location.search).get(
        "payment_intent_lient_secret"
      );
      const { error, paymentIntent } = await stripe.retrievePaymentIntent(clientSecret);
      // const { error, paymentIntent } = await stripe.confirmPayNowPayment(clientSecret);
      setMessageBody(error ? `> ${error.message}` : 
      (
        <>&gt; Payment {paymentIntent.status}: <a href={`https://dashboard.stripe.com/test/payments/${paymentIntent.id}`} target="_blank" rel="noreferrer">{paymentIntent.id}</a></>
      ));
    });
  }, [stripePromise]);

  return (
    <>
      <h1>Thank you!</h1>
      <a href="/">home</a>
      <div id="messages" role="alert" style={messageBody ? {display: 'block'} : {}}>{messageBody}</div>
    </>
  );
}