import React, {useCallback, useEffect, useState} from "react";
import {
  MemoryRouter as Router,
  Switch,
  Route,
} from "react-router-dom";
/*import RegisterPatient from './main/webapp/components/RegisterPatient';*/
/*import PatientDashboard from './main/webapp/components/PatientDashboard';*/
import Dashboard from './main/webapp/components/Dashboard';
/*import PatientVitals from './main/webapp/components/PatientVitals';
import AddPatientVitals from './main/webapp/components/Add-Patient-Vitals';*/
import { ToastContainer} from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import './main/webapp/css/style.css'
import DualListBox from "react-dual-listbox";
import 'react-dual-listbox/lib/react-dual-listbox.css';
const options = [
    { value: 'one', label: 'Option One' },
    { value: 'two', label: 'Option Two' },
];

export default function App() {
    const [selected, setSelected] = useState(['one'])
    const onChange = (s) => {
            setSelected(s)
    };
  return (
    <Router>
        <DualListBox
            options={options}
        />
      <div>
      <ToastContainer />
        {/* A <Switch> looks through its children <Route>s and
            renders the first one that matches the current URL. */}
        <Switch>
         
{/*          <Route path="/register-patient">
            <RegisterPatientPage />
          </Route>*/}
{/*          <Route path="/patient-dashboard">
            <PatientDashboardPage />
          </Route>*/}
{/*          <Route path="/patient-vitals">
            <PatientVitalsPage />
          </Route>
          <Route path="/add-patient-vital">
            <AddPatientVitalsPage />
          </Route>*/}
          <Route path="/">
            <Dashboard />
          </Route>
          
        </Switch>
      </div>
    </Router>
  );
}

/*function PatientDashboardPage() {
  return <PatientDashboard />;
}*/
/*function RegisterPatientPage() {
  return <RegisterPatient />;
}
function PatientVitalsPage() {
  return <PatientVitals />
}
function AddPatientVitalsPage() {
  return <AddPatientVitals />
}*/


