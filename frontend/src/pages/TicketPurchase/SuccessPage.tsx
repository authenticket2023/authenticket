import { Grid, Typography } from '@mui/material';
import React, { useEffect, useState } from 'react'
import { Navigate } from 'react-router-dom';
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

export const SuccessPage: React.FC = (): JSX.Element => {

    //set parameters
    const { orderId } = useParams<{ orderId: string }>();

    useEffect(() => {
        completeOrder(orderId);
        orderInfo(orderId);
    }, []);

    const token = window.localStorage.getItem('accessToken');
    const [orderSummary, setOrderSummary] = useState<OrderSummary | undefined>();
    // const [purchaserInfo, setPurchaserInfo] = useState<PurchaserInfo | undefined>();
    // const [ticketSet, setTicketSet] = useState<TicketInfo[]>([]);

    //call backend to complete order
    const completeOrder = async (orderId: any) => {
        // //calling backend API
        fetch(`${process.env.REACT_APP_BACKEND_URL}/order/complete/${orderId}`, {
            headers: {
              'Content-Type': 'application/json',
            },
            method: 'GET',
          })
            .then(async (response) => {
              if (response.status == 200) {                
                console.log("order completed");
              } else {
                //passing to parent component
              }
            })
            .catch((err) => {
              window.alert(err);
            });
    }

    //call backend api to get order summary
    const orderInfo = async (orderId: any) => {
        console.log("hello");
        // //calling backend API
        fetch(`${process.env.REACT_APP_BACKEND_URL}/order/${orderId}`, {
            headers: {
              'Content-Type': 'application/json',
            },
            method: 'GET',
          })
            .then(async (response) => {
              if (response.status == 200) {
                const apiResponse = await response.json();
                const data = apiResponse.data;
                setOrderSummary(data);
                console.log(data);
                // setPurchaserInfo(data.purchaser);
                // setTicketSet(data.ticketSet);

              } else {
                //passing to parent component
              }
            })
            .catch((err) => {
              window.alert(err);
            });
    }

    return (
        <div>
            <NavbarLoggedIn /> 
            <Grid style={{display:'flex', flexDirection:'column', justifyContent:'center', alignItems:'center'}}>
                <Typography style={{font:'roboto', fontWeight:500, fontSize:'24px', marginLeft:0, marginTop:70, marginRight:420, marginBottom:10}}>
                    Order Summary
                </Typography>
                <Grid style={{background:'#F8F8F8', height:'500px', width:'650px', borderRadius:'8px'}}>
                    <Typography style={{font:'roboto', fontWeight:500, fontSize:'18px', marginLeft:30, marginTop:25, marginBottom:10}}>
                        Personal Details
                    </Typography>
                    <Typography>
                        Name: {orderSummary?.purchaser.name}
                    </Typography>
                </Grid>
            </Grid>
        </div>
    )
}