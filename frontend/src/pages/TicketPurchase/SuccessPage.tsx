import { Grid, Typography } from '@mui/material';
import React, { useEffect, useState } from 'react';
import { NavbarLoggedIn } from '../../Navbar';
import { useParams } from 'react-router-dom';

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

interface EnteredData {
    images: File[];
    names: string[];
    sectionID: string;
    row: string;
    seat: string;
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
  const [orderSummary, setOrderSummary] = useState<OrderSummary | undefined>();
  const [completedOrder, setCompletedOrder] = useState<CompletedOrder | undefined>();

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
          setCompletedOrder(data.event);
          submitFace();
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
    console.log('hello');
    // Calling backend API
    fetch(`${process.env.REACT_APP_BACKEND_URL}/order/${orderId}`, {
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
          console.log(data);
          // Set purchaserInfo and ticketSet as needed
        } else {
          // Handle error or pass to parent component
        }
      })
      .catch((err) => {
        window.alert(err);
      });
  };

  // Call backend API for face
const submitFace = async () => {
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
      });
    }

  };

  return (
    <div>
      <NavbarLoggedIn />
      <Grid style={{ display: 'flex', flexDirection: 'column', justifyContent: 'center', alignItems: 'center' }}>
        <Typography style={{ font: 'roboto', fontWeight: 500, fontSize: '24px', marginLeft: 0, marginTop: 70, marginRight: 420, marginBottom: 10 }}>
          Order Summary
        </Typography>
        <Grid style={{ background: '#F8F8F8', height: '500px', width: '650px', borderRadius: '8px' }}>
          <Typography style={{ font: 'roboto', fontWeight: 500, fontSize: '18px', marginLeft: 30, marginTop: 25, marginBottom: 10 }}>
            Personal Details
          </Typography>
          <Typography>
            Name: {orderSummary?.purchaser.name}
          </Typography>
        </Grid>
      </Grid>
    </div>
  );
};
