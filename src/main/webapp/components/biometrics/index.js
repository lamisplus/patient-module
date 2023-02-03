import React, {useCallback, useEffect, useState} from 'react';
import {Link, useHistory} from "react-router-dom";
import MatButton from "@material-ui/core/Button";
import {TiArrowBack} from "react-icons/ti";
import {useForm} from "react-hook-form";
import {Button, Card, CardContent, FormControl, Grid, MenuItem, Paper, TextField, Typography} from "@mui/material";
import axios from "axios";
import {token, url as baseUrl} from "../../../../api";
import {makeStyles} from "@material-ui/core/styles";
import PatientCard from "../PatientCard";
import ClientDashboard from "./index";
import PersonDemographics from "../PersonDemographics";
import Breadcrumbs from "@mui/material/Breadcrumbs";
import {Tab} from "semantic-ui-react";
import MaterialTable from "material-table";
import Biometrics from "../Biometrics";
import moment from "moment";
import Swal from "sweetalert2";

const useStyles = makeStyles((theme) => ({
    root: {
        width: '100%',
        marginBottom: 20,
        flexGrow: 1,
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
    checkinModal:{
        "& .modal-dialog":{
            maxWidth:"1000px"
        },
        "& .ui.label":{
            backgroundColor:"#fff",
            fontSize:'16px',
            color:'#014d88',
            fontWeight:'bold',
            textAlign:'left'
        },
        "& .card-title":{
            color:'#fff',
            fontWeight:'bold'
        },
        "& .form-control":{
            borderRadius:'0.25rem',
            height:'41px'
        },
        "& .card-header:first-child": {
            borderRadius: "calc(0.25rem - 1px) calc(0.25rem - 1px) 0 0"
        },
        "& .dropdown-toggle::after": {
            display: " block !important"
        },
        "& select":{
            "-webkit-appearance": "listbox !important"
        },
        "& p":{
            color:'red'
        },
        "& label":{
            fontSize:'14px',
            color:'#014d88',
            fontWeight:'bold'
        }
    },
    checkInDatePicker:{
        '& .MuiFormControl-root.MuiTextField-root':{
            border:'1px solid #eee'
        }
    }
}));
function Index(props) {
    const userDetail = props.location && props.location.state ? props.location.state.user : null;
    const [loading, setLoading] = useState('');
    let history = useHistory();
    const classes = useStyles();
    const patientObj = history.location && history.location.state ? history.location.state.patientObj : {};
    console.log("data",patientObj)
    const permissions = history.location && history.location.state ? history.location.state.permissions : [];
    const [patientBiometricStatus, setPatientBiometricStatus]= useState(patientObj.biometricStatus);
    const [biometricsModuleInstalled,setBiometricsModuleInstalled]=useState(false);
    const [patientVisits, setPatientVisits]= useState([]);
    const [checkinStatus, setCheckinStatus]= useState(false)

    const updatePatientBiometricStatus = (bioStatus) =>{
        setPatientBiometricStatus(bioStatus);
    }
    const panes = [
//        { menuItem: 'Patient Biometric Details', render: () =>
//                <Tab.Pane>
//                    <p>Patient biometric captured finger prints </p>
//                </Tab.Pane>
//        },

        { menuItem: permissions.includes('view_patient_appointment') && biometricsModuleInstalled || permissions.includes("all_permission")  && biometricsModuleInstalled? 'Biometrics' : "", render: () =>
                permissions.includes('view_patient_appointment') || permissions.includes("all_permission") ?
                    <Tab.Pane>
                        <div style={{ minHeight: 400, width: '100%' }}>
                            <Biometrics patientObj={patientObj} patientId={patientObj.id} updatePatientBiometricStatus={updatePatientBiometricStatus}/>
                        </div>
                    </Tab.Pane>
                    :""
        }



    ];
    const checkForBiometricsModule =()=>{
        axios
            .get(`${baseUrl}modules/check?moduleName=biometric`,
                { headers: {"Authorization" : `Bearer ${token}`} }
            )
            .then((response) => {
                if(response.data===true){
                    setBiometricsModuleInstalled(true);
                }
            })
            .catch((error) => {
                //console.log(error);
            });

    }

    useEffect(() => {
        checkForBiometricsModule();
    }, []);
    return (
        <div className={classes.root}>
            <div className="mb-6 col-md-6" style={{paddingTop:"10px",paddingBottom:'10px'}} >
                <Breadcrumbs aria-label="breadcrumb">
                    <Typography style={{color:'#992E62'}}>Patient</Typography>
                    <Typography style={{color:'#014d88'}}>Dashboard</Typography>
                </Breadcrumbs>

            </div>
            <Card>
                <CardContent>
                    <PersonDemographics patientObj={patientObj} permissions={permissions} patientBiometricStatus={patientObj.biometricStatus}/>
                    <Card style={{marginTop:'5px'}}>
                       <div>
                            <Link to={"/"} >
                                <MatButton
                                    className=" float-right mr-1"
                                    variant="contained"
                                    floated="left"
                                    startIcon={<TiArrowBack  />}
                                    style={{backgroundColor:"rgb(153, 46, 98)", color:'#fff', height:'35px'}}
                                >
                                    <span style={{ textTransform: "capitalize" }}>Back</span>
                                </MatButton>
                            </Link>
                        </div>
                        <CardContent>
                            <Tab panes={panes} />
                        </CardContent>
                    </Card>
                </CardContent>
            </Card>

        </div>
    );
}

export default Index;