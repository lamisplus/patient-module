import React from "react";
import {
  MemoryRouter as Router,
  Switch,
  Route,
} from "react-router-dom";
import RegisterPatient from './main/webapp/components/RegisterPatient';
import PatientDashboard from './main/webapp/components/PatientDashboard';
import Dashboard from './main/webapp/components/Dashboard';
import { ToastContainer} from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

export default function App() {
  return (
    <Router>
      <div>
      <ToastContainer />
        {/* A <Switch> looks through its children <Route>s and
            renders the first one that matches the current URL. */}
        <Switch>
         
          <Route path="/register-patient">
            <RegisterPatientPage />
          </Route>
          <Route path="/patient-dashboard">
            <PatientDashboardPage />
          </Route>
          <Route path="/">
            <Dashboard />
          </Route>
          
        </Switch>
      </div>
    </Router>
  );
}

function PatientDashboardPage() {
  return <PatientDashboard />;
}
function RegisterPatientPage() {
  return <RegisterPatient />;
}


