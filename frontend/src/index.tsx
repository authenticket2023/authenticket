import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import { config as dotEnvConfig } from 'dotenv';
import 'bootstrap/dist/css/bootstrap.css';
import "@fortawesome/fontawesome-free/css/all.min.css";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { createRoot } from 'react-dom/client';
//for normal user
import { About } from './pages/About';
import { Forbidden } from './pages/Forbidden';
import { Event } from './pages/Event';
import { FAQ } from './pages/FAQ';
import { Home } from './pages/Home';
import { Login } from './pages/Login';
import { Page404 } from './pages/Page404';
import { Profile } from './pages/Profile';
import { Signup } from './pages/Signup';
import { Support } from './pages/Support';
import { Venue } from './pages/Venue';
import { EventDetails } from './pages/EventDetails';
import { TicketPurchase } from './pages/TicketPurchase';
import { SuccessPage } from './pages/TicketPurchase/SuccessPage';
import { CancelPage } from './pages/TicketPurchase/CancelPage';
//for organiser user
import { HomeOrganiser } from './pages/HomeOrganiser';
import { EventOrganiser } from './pages/EventOrganiser';
import { OrganiserSignup } from './pages/OrganiserSignup';
import { OrganiserLogin } from './pages/OrganiserLogin';
import { CheckinOrganiser } from './pages/CheckinOrganiser';
//for admin user
import { HomeAdmin } from './pages/HomeAdmin';
import { EventAdmin } from './pages/EventAdmin';
import { ArtistAdmin } from './pages/ArtistAdmin';
import { VenueAdmin } from './pages/VenueAdmin';
import { AdminLogin } from './pages/AdminLogin';

const container: any = document.getElementById('root');
const root = createRoot(container);
root.render(
	<BrowserRouter >
		<Routes >
			<Route path="/Home" element={<Home />} />
			<Route path="/" element={<Navigate to="/Home" />} />
			<Route path="/About" element={<About />} />
			<Route path="/Login" element={<Login />} />
			<Route path="/404" element={<Page404 />} />
			<Route path="/Profile" element={<Profile />} />
			<Route path="/Signup" element={<Signup />} />
			<Route path="/Event" element={<Event />} />
			<Route path="/FAQ" element={<FAQ />} />
			<Route path="/Support" element={<Support />} />
			<Route path="/Venue" element={<Venue />} />
			<Route path="/Forbidden" element={<Forbidden />} />
			<Route path="/*" element={<Page404 />} />
			<Route path="/HomeOrganiser" element={<HomeOrganiser />} />
			<Route path="/EventOrganiser" element={<EventOrganiser />} />
			<Route path="/CheckinOrganiser" element={<CheckinOrganiser />} />
			<Route path="/HomeAdmin" element={<HomeAdmin />} />
			<Route path="/EventAdmin" element={<EventAdmin />} />
			<Route path="/VenueAdmin" element={<VenueAdmin />} />
			<Route path="/ArtistAdmin" element={<ArtistAdmin />} />
			<Route path="/OrganiserSignup" element={<OrganiserSignup />} />
			<Route path="/OrganiserLogin" element={<OrganiserLogin />} />
			<Route path="/AdminLogin" element={<AdminLogin />} />
			<Route path="/EventDetails/:eventId" element={<EventDetails />} />
			<Route path="/TicketPurchase/:eventId" element={<TicketPurchase />} />
			<Route path="/success/:orderId" element={<SuccessPage />} />
			<Route path="/cancel/:orderId" element={<CancelPage />} />
		</Routes>
	</BrowserRouter>);


