import React, { useState, useEffect, useRef } from "react";
import {Modal,ModalHeader, ModalBody,Form,
    Row,Label,Card,CardBody,Col, FormGroup,CardHeader, Input
} from "reactstrap";

import { makeStyles } from "@material-ui/core/styles";
import Select from "react-select";
import "react-toastify/dist/ReactToastify.css";
import "react-widgets/dist/css/react-widgets.css";
import SaveIcon from "@material-ui/icons/Save";
import MatButton from "@material-ui/core/Button";
import FingerprintIcon from '@material-ui/icons/Fingerprint';
import { Button2, Icon, List } from 'semantic-ui-react'
import {ToastContainer, toast} from "react-toastify";
import ThumbUpIcon from '@material-ui/icons/ThumbUp';
import axios from "axios";
import {token, url as baseUrl} from "../../../api";

import { green, red } from '@mui/material/colors';
import { Dimmer, Loader, Image, Segment } from 'semantic-ui-react'

import Box from '@mui/material/Box';
import CircularProgress from '@mui/material/CircularProgress';
import Button from '@mui/material/Button';
import Fab from '@mui/material/Fab';
import CheckIcon from '@mui/icons-material/Check';
//import SaveIcon from '@mui/icons-material/Save';
// import LinearProgress from '@mui/material/LinearProgress';
import Typography from '@mui/material/Typography';
// import CancelIcon from '@mui/icons-material/Cancel';
// import ModalImage from "react-modal-image";
import {Link, useHistory} from 'react-router-dom';
import moment from "moment";
import {Dropdown,Badge} from 'react-bootstrap';
import { Alert, AlertTitle } from '@material-ui/lab';
import fingerprintimage  from '../images/fingerprintimage.png';
import DeleteIcon from "@material-ui/icons/Delete";
import UpgradeIcon from '@mui/icons-material/Upgrade';

const useStyles = makeStyles((theme) => ({
    root: {
        width: '100%',
        "& .dropdown-toggle::after, .dropleft .dropdown-toggle::before, .dropright .dropdown-toggle::before, .dropup .dropdown-toggle::after":{
            fontFamily:'FontAwesome',
            border:'0',
            verticalAlign:'middle',
            marginLeft:'.25em',
            lineHeight:'1'
        },
        "& .dropdown-menu .dropdown-item":{
            fontSize: '16px',
            color: '#014d88',
            padding: '0.5rem 1.75rem',
            fontWeight:'bold'
        },
        "& .mt-4":{
            marginTop:'28px !important'
        },
        "& .form-control":{
            color:'#992E62'
        },
        "& .form-control:focus":{
            color:'#014d88'
        }
    },
    card: {
        margin: theme.spacing(20),
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
    },
/*    form: {
        width: "100%", // Fix IE 11 issue.
        marginTop: theme.spacing(3),
    },
    submit: {
        margin: theme.spacing(3, 0, 2),
    },
    cardBottom: {
        marginBottom: 20,
    },
    Select: {
        height: 45,
        width: 350,
    },
    button: {
        margin: theme.spacing(1),
    },
    root: {
        "& > *": {
            margin: theme.spacing(1),
        },
    },
    input: {
        display: "none",
    },
    error: {
        color: "#f85032",
        fontSize: "12.8px",
    },*/
}));

let checkUrl=""

function Biometrics(props) {
    const classes = useStyles();
    const [biometricDevices,setbiometricDevices] = useState([]);
    const [objValues, setObjValues]= useState({biometricType: "FINGERPRINT", patientId:props.patientId, templateType:"", device:""})
    const [fingerType, setFingerType] = useState([]);
    const [devices, setDevices] = useState([]);
    const [loading, setLoading] = React.useState(false);
    const [pageLoading, setPageLoading] = useState(false);
    const [showCapture, setshowCapture] = React.useState(false);
    const [tryAgain, setTryAgain] = React.useState(false);
    const [success, setSuccess] = React.useState(false);
    const [errors, setErrors] = useState({});
    const [storedBiometrics, setStoredBiometrics] = useState([]);
    // const [responseImage, setResponseImage] = useState("")
    const [capturedFingered, setCapturedFingered]= useState([])
    console.log(biometricDevices)
    const buttonSx = {
        ...(success && {
            bgcolor: green[500],
            '&:hover': {
                bgcolor: green[700],
            },
        }),
    };

    const getPersonBiometrics = () =>{
        axios.get(`${baseUrl}biometrics/person/${props.patientId}`,{ headers: {"Authorization" : `Bearer ${token}`}}).then((response)=>{
            console.log('person biometrics')
            if(response.data.length > 0){
                setStoredBiometrics(response.data)
            }
            setPageLoading(true);
            console.log(response.data)
            console.log(response.data.length)
            console.log('person biometrics');
        }).catch((error)=>{
            console.log("getPersonBiometrics error")
            console.log(error)
            console.log("getPersonBiometrics error");
            setPageLoading(true);
        })
    }


    useEffect(() => {
        getPersonBiometrics();
        TemplateType();
        biometricFingers();
    }, []);
    //Get list of KP
    const TemplateType =()=>{
        axios
            .get(`${baseUrl}modules/check?moduleName=biometric`,
                { headers: {"Authorization" : `Bearer ${token}`} }
            )
            .then((response) => {
                console.log("Template Type");
                console.log(response.data);
                console.log("Template Type");
                if(response.data===true){
                    axios
                        .get(`${baseUrl}biometrics/devices`,
                            { headers: {"Authorization" : `Bearer ${token}`} }
                        )
                        .then((response) => {
                            setDevices(response.data);
                            setbiometricDevices(response.data);

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

    //Get list of Finger index
    const biometricFingers =()=>{
        axios
            .get(`${baseUrl}application-codesets/v2/BIOMETRIC_CAPTURE_FINGERS`,
                { headers: {"Authorization" : `Bearer ${token}`} }
            )
            .then((response) => {
                console.log("FingerType");
                console.log(response.data);
                console.log("FingerType");
                setFingerType(response.data);
            })
            .catch((error) => {
            });

    }
    //check if device is plugged or not
    const checkDevice = e =>{
        const deviceName =e.target.value;
        const selectedDevice=biometricDevices.find((x)=> x.name ===deviceName )
        checkUrl= selectedDevice.url===null? baseUrl : selectedDevice.url
        console.log(checkUrl)
        setObjValues({...objValues, device:deviceName})
        axios
            .get(`${checkUrl}biometrics/secugen/boot?reader=${deviceName}`,
                { headers: {"Authorization" : `Bearer ${token}`} }
            )
            .then((response) => {
                if(response.data.errorType ==="ERROR"){
                    toast.error(response.data.errorName + " is not plug");
                    //setshowCapture(true)
                }else{
                    setshowCapture(true)
                }
            })
            .catch((error) => {
            });
    }
    // handle the input changes

    const handleInputChange = e => {
        setObjValues ({...objValues,  [e.target.name]: e.target.value});
    }
    //form validation
    const validate = () => {
        let temp = { ...errors }
        temp.templateType = objValues.templateType ? "" : "This field is required"
        temp.device = objValues.device ? "" : "This field is required"
        setErrors({
            ...temp
        })
        return Object.values(temp).every(x => x == "")
    }
    //to capture  selected index finger
    const captureFinger = (e) => {
        e.preventDefault();
        if(validate()){
            axios.post(`${checkUrl}biometrics/secugen/enrollment?reader=SG_DEV_AUTO`,objValues,
                { headers: {"Authorization" : `Bearer ${token}`}},
            )
                .then(response => {
                    setLoading(true);
                    if(response.data.type ==="ERROR"){
                        setLoading(false);
                        setTryAgain(true);
                        window.setTimeout(() => {
                            setTryAgain(false);
                        }, 5000);
                        toast.error(response.data.message.ERROR);
                    }else{
                        const templateType= response.data.templateType
                        setTryAgain(false);
                        setSuccess(true)
                        window.setTimeout(() => {
                            setSuccess(false)
                            setLoading(false);
                        }, 5000);

                        setCapturedFingered([...capturedFingered, response.data])
                        fingerType.splice(templateType, 1);
                        setFingerType([...fingerType]);
                    }
                    //toast.success("Record save successful");
                })
                .catch(error => {
                });
        }
    }

    //Save Biometric capture
    const saveBiometrics = (e) => {
        e.preventDefault();
        if(capturedFingered.length > 1){
            const capturedObj= capturedFingered[capturedFingered.length - 1]
            axios.post(`${baseUrl}biometrics/templates`,capturedObj,
                { headers: {"Authorization" : `Bearer ${token}`}},
            )
                .then(response => {
                    toast.success("Biometric save successful");
                    setCapturedFingered([])
                    props.setPatientBiometricStatus(true)
                    props.togglestatus()
                })
                .catch(error => {
                    toast.error("Something went wrong");
                });
        }else{

            toast.error("You can't save less than 2 finger");
        }
    }

    return (
        <div className={classes.root}>


            <div className="row">
                <div className="row col-8" style={{padding:'5px'}}>
                    {pageLoading && storedBiometrics.length > 0 ? (
                            <>
                                {storedBiometrics.map((biometric, index)=>(
                                    <div  className="col-xl-4 col-xxl-4 col-lg-4 col-md-4 col-sm-6" key={index} style={{minHeight:'300px'}}>
                                        <div  className="card " style={{borderRadius:"6px"}}>

                                            <div className="card-header align-items-start" style={{backgroundColor:'#fff'}}>
                                                <div>
                                                    <h6 className="fs-18 font-w500 mb-3"><Link to={"#"}className="user-name" style={{color:'#014d88',fontSize:'20px',fontFamily:`"poppins",sans-serif`}}>{biometric.templateType}</Link></h6>
                                                    <div className="fs-14 text-nowrap" style={{color:'#992E62', fontWeight:'bold',fontFamily:`"poppins",sans-serif`}}><i className="fa fa-calendar-o me-3" aria-hidden="true"></i>{moment(biometric.lastModifiedDate).format("YYYY-MM-DD HH:mm")}</div>
                                                </div>

                                                {/*Action button -- Dropdown menu*/}
                                                <Dropdown className="dropdown ms-auto"  >
                                                    <Dropdown.Toggle
                                                        as="button"
                                                        variant=""
                                                        drop="up"
                                                        className="btn sharp btn-primary "
                                                        id="tp-btn"
                                                        style={{ backgroundColor: '#014d88', borderColor:'#014d88', fontSize:"4", borderRadius:'5px',marginRight:'-18px',marginTop:'-10px'}}
                                                    >
                                                        <svg
                                                            xmlns="http://www.w3.org/2000/svg"
                                                            xmlnsXlink="http://www.w3.org/1999/xlink"
                                                            width="18px"
                                                            height="18px"
                                                            viewBox="0 0 24 24"
                                                            version="1.1"
                                                        >
                                                            <g
                                                                stroke="none"
                                                                strokeWidth="1"
                                                                fill="none"
                                                                fillRule="evenodd"
                                                            >
                                                                <rect x="0" y="0" width="24" height="24" />
                                                                <circle fill="#ffffff" cx="12" cy="5" r="2" />
                                                                <circle fill="#ffffff" cx="12" cy="12" r="2" />
                                                                <circle fill="#ffffff" cx="12" cy="19" r="2" />
                                                            </g>
                                                        </svg>
                                                    </Dropdown.Toggle>
                                                    <Dropdown.Menu alignRight={true} className="dropdown-menu-right">
                                                        <Dropdown.Item /*onClick={()=>viewInstallModule(contact)}*/>
                                                            <UpgradeIcon/> Update
                                                        </Dropdown.Item>
                                                        <Dropdown.Item style={{color:'red'}}/*onClick={()=>updateModuleInformation(contact)}*/>
                                                            <DeleteIcon /> Delete
                                                        </Dropdown.Item>

                                                    </Dropdown.Menu>
                                                </Dropdown>

                                            </div>
                                            <div className="card-body p-0 pb-3">
                                                <ul className="list-group list-group-flush">
                                                    <li className="list-group-item" style={{height:'200px', display:'flex',justifyContent:'center'}}>
                                                        <img src={fingerprintimage}  alt="" style={{height:'180px'}} />
                                                    </li>

                                                    <li className="list-group-item">

                                                        <Badge variant="info badge-xs light" className="card-link float-end">Version {/*{contact.version}*/}</Badge>
                                                        <span className="mb-0 title">Status {biometric.iso}</span> :
                                                        <span className="text-black desc-text ms-2">
                                                        <Badge variant={biometric.iso===true? "primary badge-xs": "danger badge-xs"}><i className="fa fa-check-square me-2 scale4" aria-hidden="true"></i> </Badge>
                                                    </span>
                                                    </li>

                                                </ul>
                                            </div>

                                        </div>
                                    </div>

                                ))}
                            </>
                        )
                        :
                        <>
                            <div className="row">
                                <div  className="col-xl-12 col-xxl-12 col-lg-12 col-md-12 col-sm-12"  >
                                    <Alert severity="info">
                                        <AlertTitle style={{height:'400px'}}>
                                            <strong>No biometrics captured</strong>
                                        </AlertTitle>
                                    </Alert>
                                </div>
                            </div>
                        </>
                    }
                </div>
                <div className="row col-4" style={{padding:'5px',marginLeft:'5px',border:'1px solid rgba(99, 99, 99, 0.2)',boxShadow: 'rgba(99, 99, 99, 0.2) 0px 2px 8px 0px'}}>
                    <div className="col-12">
                        <ToastContainer />
                        <Col md={12}>
                            <FormGroup>
                                <Label for='device' style={{color:'#014d88',fontWeight:'bold',fontSize:'14px' }}>Select Device </Label>
                                <Input
                                    type="select"
                                    name="device"
                                    id="device"
                                    onChange={checkDevice}
                                    value={objValues.device}
                                    required
                                >
                                    <option value="">Select Device </option>
                                    {biometricDevices.map(({ id, name }) => (
                                        <option key={id} value={name}>
                                            {name}
                                        </option>
                                    ))}

                                </Input>
                                {errors.device !=="" ? (
                                    <span className={classes.error}>{errors.device}</span>
                                ) : "" }
                            </FormGroup>
                        </Col>
                        {showCapture ? (
                                <div className="row col-12">
                                    <Col md={6}>
                                        <FormGroup>
                                            <Label for='device' style={{color:'#014d88',fontWeight:'bold',fontSize:'14px' }}>Select Finger</Label>
                                            <Input
                                                type="select"
                                                name="templateType"
                                                id="templateType"
                                                onChange={handleInputChange}
                                                value={objValues.templateType}
                                                required
                                            >
                                                <option value="">Select Finger </option>

                                                {fingerType.map((value) => (
                                                    <option key={value.id} value={value.display}>
                                                        {value.display}
                                                    </option>
                                                ))}
                                            </Input>
                                            {errors.templateType !=="" ? (
                                                <span className={classes.error}>{errors.templateType}</span>
                                            ) : "" }
                                        </FormGroup>
                                    </Col>


                                    <Col md={6}>

                                        <MatButton
                                            type='button'
                                            variant='contained'
                                            color='primary'
                                            onClick={captureFinger}
                                            className={'mt-4'}
                                            style={{backgroundColor:'#992E62'}}
                                            startIcon={<FingerprintIcon />}
                                        >
                                            Capture Finger
                                        </MatButton>

                                    </Col>
                                    <br/>

                                </div>
                            )
                            :
                            ""
                        }

                        <Row>
                            {capturedFingered.length >=1 ? (
                                    <>
                                        <Col md={12} style={{marginTop:"20px"}}>
                                            <List celled horizontal>
                                                {capturedFingered.map((x) => (
                                                    <List.Item style={{width:'200px',height:'200px',display:'flex', justifyContent:'center',alignItems: 'center'}}>
                                                        <List.Header><Icon name='cancel'  color='red' /> </List.Header>
                                                        <List.Content> <FingerprintIcon style={{color:"#2E7D32", fontSize: 40}}/>{x.templateType}</List.Content>
                                                    </List.Item>
                                                ))}

                                            </List>

                                        </Col>
                                        <br/><br/><br/><br/><br/><br/>
                                        <Col md={12} >
                                            <MatButton
                                                type='button'
                                                variant='contained'
                                                color='primary'
                                                onClick={saveBiometrics}
                                                // className={classes.button}
                                                startIcon={<SaveIcon/>}
                                            >
                                                Save Capture
                                            </MatButton>
                                        </Col>
                                    </>
                                )
                                :
                                ""
                            }
                        </Row>
                    </div>
                </div>

            </div>

























{/*
            <Form >

                    <Card>
                         <CardHeader>
                                    Capture Biometrics
                                </CardHeader>
                        <CardBody>
                            <Row form>
                                <ToastContainer />
                                <Col md={4}>
                                    <FormGroup>
                                        <Label for='device'>Select Device </Label>
                                        <Input
                                            type="select"
                                            name="device"
                                            id="device"
                                            onChange={checkDevice}
                                            value={objValues.device}
                                            required
                                        >
                                            <option value="">Select Device </option>
                                            {biometricDevices.map(({ id, name }) => (
                                                <option key={id} value={name}>
                                                    {name}
                                                </option>
                                            ))}

                                        </Input>
                                        {errors.device !=="" ? (
                                            <span className={classes.error}>{errors.device}</span>
                                        ) : "" }
                                    </FormGroup>
                                </Col>
                                {showCapture ? (
                                        <>
                                            <Col md={4}>
                                                <FormGroup>
                                                    <Label for='device'>Select Finger</Label>
                                                    <Input
                                                        type="select"
                                                        name="templateType"
                                                        id="templateType"
                                                        onChange={handleInputChange}
                                                        value={objValues.templateType}
                                                        required
                                                    >
                                                        <option value="">Select Finger </option>

                                                        {fingerType.map((value) => (
                                                            <option key={value.id} value={value.display}>
                                                                {value.display}
                                                            </option>
                                                        ))}
                                                    </Input>
                                                    {errors.templateType !=="" ? (
                                                        <span className={classes.error}>{errors.templateType}</span>
                                                    ) : "" }
                                                </FormGroup>
                                            </Col>


                                            <Col md={4}>

                                                <MatButton
                                                    type='button'
                                                    variant='contained'
                                                    color='primary'
                                                    onClick={captureFinger}
                                                    className={'mt-4'}
                                                    startIcon={<FingerprintIcon />}
                                                >
                                                    Capture Finger
                                                </MatButton>

                                            </Col>
                                            <br/>

                                        </>
                                    )
                                    :
                                    ""
                                }
                                 <img width='500' height='200' src={responseImage}/>
                                <Col sm="12" md={{ size: 6, offset: 3 }}>

                                    {loading && (
                                        <>
                                            <Box sx={{ display: 'flex', alignItems: 'center' }}>
                                                <Box sx={{ m: 1, position: 'relative' }}>
                                                    <Fab
                                                        aria-label="save"
                                                        color="secondary"
                                                        sx={buttonSx}

                                                    >
                                                        {success ? <CheckIcon /> : <FingerprintIcon />}
                                                    </Fab>
                                                    {loading && (
                                                        <CircularProgress
                                                            size={68}
                                                            sx={{
                                                                color: green[500],
                                                                position: 'absolute',
                                                                top: -6,
                                                                left: -6,
                                                                zIndex: 1,
                                                            }}
                                                        />
                                                    )}
                                                </Box>
                                                <Typography variant="h6" gutterBottom component="div">
                                                    {success ?` your  ${objValues.templateType} Finger captured.` : `Please place your  ${objValues.templateType} Finger on scanner.`}
                                                </Typography>
                                            </Box>
                                        </>
                                    )
                                    }
                                    {tryAgain && (
                                        <>


                                            <Box sx={{ display: 'flex', alignItems: 'center' }}>
                                                <Box sx={{ m: 1, position: 'relative' }}>
                                                    <Fab
                                                        aria-label="save"
                                                        color="secondary"
                                                        sx={buttonSx}

                                                    >
                                                        <FingerprintIcon />
                                                    </Fab>
                                                    {tryAgain && (
                                                        <CircularProgress
                                                            size={68}
                                                            sx={{
                                                                color: red[500],
                                                                position: 'absolute',
                                                                top: -6,
                                                                left: -6,
                                                                zIndex: 1,
                                                            }}
                                                        />
                                                    )}
                                                </Box>
                                                <Typography variant="h6" gutterBottom component="div">
                                                    Click to Recapture Again
                                                </Typography>
                                            </Box>
                                        </>
                                    )
                                    }

                                </Col>


                            </Row>

                            <Row>
                                {capturedFingered.length >=1 ? (
                                        <>
                                            <Col md={12} >
                                                <h3>Captured Fingerprints</h3>
                                            </Col>
                                            <Col md={12} style={{marginTop:"20px"}}>
                                                <List celled horizontal>
                                                    {capturedFingered.map((x) => (
                                                        <List.Item style={{width:'200px',height:'200px',display:'flex', justifyContent:'center',alignItems: 'center'}}>
                                                             <List.Header><Icon name='cancel'  color='red' /> </List.Header>
                                                            <List.Content> <FingerprintIcon style={{color:"#2E7D32", fontSize: 40}}/>{x.templateType}</List.Content>
                                                        </List.Item>
                                                    ))}

                                                </List>

                                            </Col>
                                             <Col md={6} style={{marginTop:"20px"}}>
                                        <List celled horizontal>
                                            {[1,2,3].map((x) => (
                                                <List.Item>
                                                    <List.Header><Icon name='checkmark' color='green'/> </List.Header>
                                                    <List.Content > <FingerprintIcon style={{color:"#2E7D32", fontSize: 40}} /> Finger Type</List.Content>
                                                </List.Item>
                                            ))}

                                        </List>

                                    </Col>
                                            <br/><br/><br/><br/><br/><br/>
                                            <Col md={12} >
                                                <MatButton
                                                    type='button'
                                                    variant='contained'
                                                    color='primary'
                                                    onClick={saveBiometrics}
                                                    // className={classes.button}
                                                    startIcon={<SaveIcon/>}
                                                >
                                                    Save Capture
                                                </MatButton>
                                            </Col>
                                        </>
                                    )
                                    :
                                    ""
                                }
                            </Row>
                        </CardBody>
                    </Card>
            </Form>
*/}

        </div>
    );
}

export default Biometrics;