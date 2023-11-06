import { Grid, Typography, Box } from '@mui/material';
import React, { useEffect, useState } from 'react';
import { NavbarLoggedIn } from '../../Navbar';
import { useParams } from 'react-router-dom';
import DisplayOrder from '../Profile/displayOrders';
import  { format } from 'date-fns';

interface OrderSummary {
  orderId: number;
  orderAmount: number;
  purchaseDate: string;
  orderStatus: string;
  purchaser: {
    userId: number;
    name: string;
    email: string;
    dateOfBirth: string;
    profileImage: string | null; // Adjust the type if necessary
    role: string;
  };
  ticketSet: {
    ticketId: number;
    eventId: number;
    catId: number;
    sectionId: string;
    rowNo: number;
    seatNo: number;
    ticketHolder: string | null; // Adjust the type if necessary
    orderId: number;
  }[];
}

interface CompletedOrder {
  createdAt: string;
  updatedAt: string;
  deletedAt: string;
  eventId: number;
  eventName: string;
  eventDescription: string;
  eventDate: string;
  otherEventInfo: string;
  eventImage: string;
  ticketSaleDate: string;
  reviewStatus: string;
  reviewRemarks: string;
  isEnhanced: boolean;
  hasPresale: boolean;
  hasPresaleUsers: boolean;
  eventType: {
    eventTypeId: number;
    eventTypeName: string;
  };
}


function formatDate(dateString: string): string {
  const date = new Date(dateString);
  return format(date, "dd MMMM yyyy");
}

export const SuccessPage: React.FC = (): JSX.Element => {
  // Set parameters
  const { orderId } = useParams<{ orderId: string }>();
  //   const { enteredData } = useParams<EnteredData>();


  type Params = 'images' | 'names' | 'sectionID' | 'row' | 'seat';
  const params = useParams<Params>();
  const images = params.images ? JSON.parse(params.images) as File[] : [];
  const names = params.names ? JSON.parse(params.names) as string[] : [];
  const sectionID = params.sectionID;
  const row = params.row;
  const seat = params.seat;



  useEffect(() => {
    completeOrder(orderId);
    orderInfo(orderId);
  }, []);

  const token = window.localStorage.getItem('accessToken');
  const [summaryLoaded, setLoaded]: any = useState(false)
  const [orderSummary, setOrderSummary] : any = useState<OrderSummary | undefined>();


  // Call backend to complete order
  const completeOrder = async (orderId: any) => {
    // Calling backend API
    fetch(`${process.env.REACT_APP_BACKEND_URL}/order/complete/${orderId}`, {
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`,
      },
      method: 'PUT',
    })
      .then(async (response) => {
        if (response.status === 200) {
          console.log('Order completed');
          const apiResponse = await response.json();
          const data = apiResponse.data;
          console.log(apiResponse)
          setCompletedOrder(data.event);

          //only do submitFace if event is enhanced
          if (completedOrder?.isEnhanced) {
            submitFace();
          }
        } else {
          // Handle error or pass to parent component
        }
      })
      .catch((err) => {
        window.alert(err);
      });
  };

  // Call backend API to get order summary
  const orderInfo = async (orderId: any) => {

    // Calling backend API
    await fetch(`${process.env.REACT_APP_BACKEND_URL}/order/${orderId}`, {
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`,
      },
      method: 'GET',
    })
      .then(async (response) => {
        if (response.status === 200) {
          const apiResponse = await response.json();
          const data = apiResponse.data;
          setOrderSummary(data);
          setLoaded(true);
        }
      })
      .catch((err) => {
        window.alert(err);
      }); 
  };

  // Call backend API for face
  const submitFace = async () => {
    console.log("face")
    // Create order
    const formData = new FormData();
    formData.append('eventID', String(completedOrder?.eventId));

    // Check if orderSummary and its ticketSet are defined
    if (orderSummary && orderSummary.ticketSet) {
      // Iterate through ticketSet
      orderSummary.ticketSet.forEach((ticket, index) => {
        const { sectionId, rowNo, seatNo } = ticket;

        // Check if corresponding image and name exist
        if (images[index] && names[index]) {
          // Append the image using 'image' + (index + 1) as the key
          formData.append(`image${index + 1}`, images[index]);

          // Append the info using 'info' + (index + 1) as the key
          const info = `${names[index]},${sectionId},${rowNo},${seatNo}`;
          formData.append(`info${index + 1}`, info);
        }

        //add in email for facial api to verify the token
        formData.append('email', currUserEmail);

        //call backend
        fetch(`${process.env.REACT_APP_FACIAL_URL}/face/facial-creation`, {
          headers: {
            'Authorization': `Bearer ${token}`,
          },
          method: 'POST',
          body: formData
        })
          .then(async (response) => {
            if (response.status == 200 || response.status == 201) {
              // Parse the JSON response
              const responseData = await response.json();
              // Access the orderId from the response data
              const orderId = responseData.data.orderId;

            } else {

            }
          })
          .catch((err) => {
            window.alert(err);
          })

      });
    }

  };

  return (
    <div>
      <NavbarLoggedIn />
      {summaryLoaded ?
      <Grid style={{ display: 'flex', flexDirection: 'column', justifyContent: 'center', alignItems: 'center' }}>
        <Typography variant = 'h3' margin = {5}>
          Order Summary
        </Typography>
      <Box width= {800}>
        <DisplayOrder order = {orderSummary}/>
      </Box>
      </Grid>
  : null}
    </div>
  );
};
