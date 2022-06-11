import React,{useState, useEffect} from 'react';
import PropTypes from 'prop-types';
import {withStyles} from '@material-ui/core/styles';
import {Accordion, AccordionActions, AccordionDetails, AccordionSummary} from '@material-ui/core';
import Typography from '@material-ui/core/Typography';
import Divider from '@material-ui/core/Divider';
import {Button, Label} from 'semantic-ui-react';
import 'semantic-ui-css/semantic.min.css';
import {Col, Row, Modal} from "reactstrap";
import CaptureBiometric from './CaptureBiometric';
import axios from "axios";
import {token, url as baseUrl} from "../../../api";

const styles = theme => ({
    root: {
        width: '100%',
    },
    heading: {
        fontSize: theme.typography.pxToRem(15),
    },
    secondaryHeading: {
        fontSize: theme.typography.pxToRem(15),
        color: theme.palette.text.secondary,
    },
    icon: {
        verticalAlign: 'bottom',
        height: 20,
        width: 20,
    },
    details: {
        alignItems: 'center',
    },
    column: {
        flexBasis: '20.33%',
    },
    helper: {
        borderLeft: `2px solid ${theme.palette.divider}`,
        padding: `${theme.spacing(1)}px ${theme.spacing(1) * 2}px`,
    },
    link: {
        color: theme.palette.primary.main,
        textDecoration: 'none',
        '&:hover': {
            textDecoration: 'underline',
        },
    },
});

function PatientCard(props) {
    const { classes } = props;
    const patientObj = props.patientObj ? props.patientObj : {};
    const [modal, setModal] = useState(false) //Modal to collect sample 
    const toggleModal = () => setModal(!modal)

    const [biometricStatus, setBiometricStatus] = useState(false);
    const [devices, setDevices] = useState([]);
    useEffect(() => {         
        TemplateType();
      }, []);
     //Get list of KP
     const TemplateType =()=>{
        axios
           .get(`${baseUrl}modules/check?moduleName=biometric`,
               { headers: {"Authorization" : `Bearer ${token}`} }
           )
           .then((response) => {
               console.log(response.data);
               setBiometricStatus(response.data);
               if(response.data===true){
                axios
                    .get(`${baseUrl}biometrics/devices`,
                        { headers: {"Authorization" : `Bearer ${token}`} }
                    )
                    .then((response) => {
                        setDevices(response.data);
                        
                    })
                    .catch((error) => {
                        console.log(error)
                    });
               
                }
           })
           .catch((error) => {
           //console.log(error);
           });
       
     }
    const getHospitalNumber = (identifier) => {
        const hospitalNumber = identifier.identifier.find(obj => obj.type == 'HospitalNumber');
        return hospitalNumber ? hospitalNumber.value : '';
    };

    const calculate_age = dob => {
        const today = new Date();
        const dateParts = dob.split("-");
        const birthDate = new Date(dob); // create a date object directlyfrom`dob1`argument
        let age_now = today.getFullYear() - birthDate.getFullYear();
        const m = today.getMonth() - birthDate.getMonth();
        if (m < 0 || (m === 0 && today.getDate() < birthDate.getDate())) {
            age_now--;
        }
        if (age_now === 0) {
            return m + " month(s)";
        }
        return age_now + " year(s)";
    };

    const getPhone = (contactPoint) => {
        const phoneContact = contactPoint.contactPoint.find(obj => obj.type == 'phone');
        return phoneContact ? phoneContact.value : '';
    };

    const getAddress = (address) => {
        const city = address && address.address && address.address.length > 0 ? address.address[0].city : null;
        return city;
    };


    const handleBiometricCapture = (id) => { 
        let patientObjID= id
        setModal(!modal) 
    }

    return (
        <div className={classes.root}>
            <Accordion defaultExpanded>
                <AccordionSummary>

                    <Row>

                        <Col md={11}>
                            <Row className={"mt-1"}>
                                <Col md={12} className={classes.root2}>
                                    <b style={{fontSize: "25px"}}>
                                        {patientObj.surname + ", " + patientObj.firstName + " " + patientObj.otherName}
                                    </b>

                                </Col>
                                <Col md={4} className={classes.root2}>
                                    <span>
                                        {" "}
                                        Hospital Number : <b>{getHospitalNumber(patientObj.identifier) }</b>
                                    </span>
                                </Col>

                                <Col md={4} className={classes.root2}>
                                    <span>
                                        Date Of Birth : <b>{patientObj.dateOfBirth }</b>
                                    </span>
                                </Col>
                                <Col md={4} className={classes.root2}>
                                <span>
                                    {" "}
                                    Age : <b>{calculate_age(patientObj.dateOfBirth) }</b>
                                </span>
                                </Col>
                                <Col md={4}>
                                <span>
                                    {" "}
                                    Gender :{" "}
                                    <b>{patientObj.gender.display }</b>
                                </span>
                                <Label color={"green"} size={"mini"}>
                                    Patient Status
                                <Label.Detail>Active</Label.Detail>
                                </Label>
                                </Col>
                                <Col md={4} className={classes.root2}>
                                <span>
                                    {" "}
                                    Phone Number : <b>{getPhone(patientObj.contactPoint) }</b>
                                </span>
                                </Col>
                                <Col md={4} className={classes.root2}>
                                <span>
                                    {" "}
                                    Address : <b>{getAddress(patientObj.address)} </b>
                                </span>
                                
                                </Col>
                                
                               
                            </Row>
                        </Col>
                    </Row>

                </AccordionSummary>
                <AccordionDetails className={classes.details}>
                    <div className={classes.column} >
                        <Button
                            color='red'
                            content='BloodType'
                            //icon='heart'
                            label={{ basic: true, color: 'red', pointing: 'left', content: 'AB+' }}
                        />

                    </div>
                    <div className={classes.column}>
                        <Button
                            basic
                            color='blue'
                            content='Height'
                            icon='fork'
                            label={{
                                as: 'a',
                                basic: true,
                                color: 'blue',
                                pointing: 'left',
                                content: '74.5 in',
                            }}
                        />
                    </div>
                    <div className={classes.column}>
                        <Button
                            basic
                            color='blue'
                            content='Weight'
                            icon='fork'
                            label={{
                                as: 'a',
                                basic: true,
                                color: 'blue',
                                pointing: 'left',
                                content: '74.5 in',
                            }}
                        />
                    </div>
                    {biometricStatus===true ? (
                        <>
                            <div >
                                <Typography variant="caption">
                                    <Label color={"red"} size={"mini"}>
                                        Biometric Status
                                        <Label.Detail>Not Captured</Label.Detail>
                                    </Label>
                                    <Label as='a' color='teal' onClick={() => handleBiometricCapture(patientObj.id)} tag>
                                        Capture Now
                                    </Label>
                                    
                                </Typography>
                            </div>
                        </>
                        )
                        :""
                    }
                </AccordionDetails>
                <Divider />
                <AccordionActions>

                </AccordionActions>
            </Accordion>
            <CaptureBiometric modalstatus={modal} togglestatus={toggleModal} patientId={patientObj.id} biometricDevices={devices}/>
        </div>
    );
}

PatientCard.propTypes = {
    classes: PropTypes.object.isRequired,
};

export default withStyles(styles)(PatientCard);
