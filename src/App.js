import React from "react";
import {
  MemoryRouter as Router,
  Switch,
  Route,
} from "react-router-dom";
import RegisterPatient from './main/webapp/components/RegisterPatient';
import PatientDashboard from './main/webapp/components/PatientDashboard';
import Dashboard from './main/webapp/components/Dashboard';
import PatientVitals from './main/webapp/components/PatientVitals';
import AddPatientVitals from './main/webapp/components/Add-Patient-Vitals';
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
          <Route path="/patient-vitals">
            <PatientVitalsPage />
          </Route>
          <Route path="/add-patient-vital">
            <AddPatientVitalsPage />
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
function PatientVitalsPage() {
  return <PatientVitals />
}
function AddPatientVitalsPage() {
  return <AddPatientVitals />
}


