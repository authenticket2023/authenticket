import "dotenv/config";

const stripe = require("stripe")(process.env.STRIPE_SECRET_KEY, {
  apiVersion: "2023-08-16",
});

export const createCheckOutSession = async (req: any, res: any) => {
  try {
    console.log(req.body)
    const { orderId, products } = req.body;

    if (!products || products.length === 0) {
      return res.status(400).json({ error: "Invalid products data" });
    }

    const finalProducts = products?.map((item: any) => ({
      price_data: {
        currency: "SGD",
        product_data: {
          name: item.name,
        },
        unit_amount: item.price * 100,
      },
      quantity: item.quantity,
    }));

    const session = await stripe.checkout.sessions.create({
      line_items: finalProducts,
      payment_method_types: ["card", "paynow", "alipay", "grabpay"],
      mode: "payment",
      success_url: `${process.env.LOAD_BALANCER_URL}/success/${orderId}`,
      cancel_url: `${process.env.LOAD_BALANCER_URL}/cancel/${orderId}`,
    });

    return res.status(200).json({ id: session.id });
  } catch (error: any) {
    return res.status(400).json({ message: String(error.message) });
  }
};
