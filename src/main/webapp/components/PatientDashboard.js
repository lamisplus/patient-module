import React, {useCallback, useEffect, useState} from 'react';
import PropTypes from 'prop-types';
import { withStyles } from '@material-ui/core/styles';
import { Link } from 'react-router-dom';
import ButtonMui from "@material-ui/core/Button";
import 'semantic-ui-css/semantic.min.css';
import { Col} from "reactstrap";
import { Step, Label, Segment, Icon } from "semantic-ui-react";
import PatientCardDetail from './PatientCard'
import { useHistory } from "react-router-dom";
import { Tab } from 'semantic-ui-react';
import { DataGrid } from '@mui/x-data-grid';
import Box from '@mui/material/Box';
import { Modal, ModalHeader, ModalBody, ModalFooter } from 'reactstrap';
import FormGroup from '@mui/material/FormGroup';
import FormControlLabel from '@mui/material/FormControlLabel';
import Checkbox from '@mui/material/Checkbox';
import axios from "axios";
import {token, url as baseUrl} from "../../../api";
import Swal from "sweetalert2";
import {Controller, useForm} from "react-hook-form";
import {Button, Card, CardContent, FormControl, Grid, MenuItem, Paper, TextField, Typography} from "@mui/material";
import {format} from 'date-fns';
import { toast} from "react-toastify";
import MaterialTable from 'material-table';
import Autocomplete from '@mui/material/Autocomplete';
import CheckBoxOutlineBlankIcon from '@mui/icons-material/CheckBoxOutlineBlank';
import CheckBoxIcon from '@mui/icons-material/CheckBox';


const icon = <CheckBoxOutlineBlankIcon fontSize="small" />;
const checkedIcon = <CheckBoxIcon fontSize="small" />;

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

const appointmentColumns = [
    { field: 'id', headerName: 'ID', width: 90 },
    {
        field: 'firstName',
        headerName: 'First name',
        width: 150,
        editable: true,
    },
    {
        field: 'lastName',
        headerName: 'Last name',
        width: 150,
        editable: true,
    },
    {
        field: 'age',
        headerName: 'Age',
        type: 'number',
        width: 110,
        editable: true,
    },
    {
        field: 'fullName',
        headerName: 'Full name',
        description: 'This column has a value getter and is not sortable.',
        sortable: false,
        width: 160,
        valueGetter: (params) =>
            `${params.row.firstName || ''} ${params.row.lastName || ''}`,
    },
];

const appointments = [
    { id: 1, lastName: 'Snow', firstName: 'Jon', age: 35 },

];


let newDate = new Date()
function PatientDashboard(props) {
    const [loading, setLoading] = useState('');
    let history = useHistory();
    const { classes } = props;
    const patientObj = history.location && history.location.state ? history.location.state.patientObj : {};
    const { handleSubmit, control } = useForm();
    const [modal, setModal] = useState(false);
    const [checkinStatus, setCheckinStatus]= useState(false)
    const [modalCheckOut, setModalCheckOut] = useState(false);
    const [services, setServices]= useState([]);
    const [selectedServices, setSelectedServices]= useState({checkInServices:""});
    const [patientVisits, setPatientVisits]= useState([]);
    const [checkOutObj, setCheckOutObj] = useState({
                                                    facilityId: 1,
                                                    personId: "",
                                                    visitEndDate:format(new Date(newDate), 'yyyy-MM-dd'),
                                                    visitStartDate:"" 
                                                })
    const [checkInObj, setCheckInObj] = useState({
        serviceIds:"",
          visitDto: {
            facilityId: 1,
            personId: patientObj.id,
            visitEndDate: "",
            visitStartDate: format(new Date(newDate), 'yyyy-MM-dd')
          }
    })

    const loadServices = useCallback(async () => {
        try {
            const response = await axios.get(`${baseUrl}patient/post-service`, { headers: {"Authorization" : `Bearer ${token}`} });
            setServices(response.data);
        } catch (e) {
            await Swal.fire({
                icon: 'error',
                title: 'Oops...',
                text: 'An error occurred fetching services!',
            });
        }
    }, []);
    const loadPatientVisits = useCallback(async () => {
        try {
            const response = await axios.get(`${baseUrl}patient/visit/visit-detail/${patientObj.id}`, { headers: {"Authorization" : `Bearer ${token}`} });
            setPatientVisits(response.data);
            response.data.map((visits)=> {
                if(visits.checkOutDate===null){
                    setCheckinStatus(true)
                }
            })


        } catch (e) {
            await Swal.fire({
                icon: 'error',
                title: 'Oops...',
                text: 'An error occurred fetching services!',
            });
        }
    }, []);

    useEffect(() => {
        loadServices();
        loadPatientVisits();
    }, [loadServices, loadPatientVisits]);

    let visitTypesRows = null;
    if (services && services.length > 0) {
        visitTypesRows = services.map((service, index) => (
            <MenuItem key={service.moduleServiceCode} value={service.moduleServiceCode}>{service.moduleServiceName}</MenuItem>
        ));
    }




    const columns = [
        {
            field: 'checkInDate',
            headerName: 'Checked In Date',
            width: 200,
            editable: false,
        },
        {
            field: 'checkOutDate',
            headerName: 'Check Out Date',
            width: 200,
            editable: false,
        },
        {
            field: 'service',
            headerName: 'Service',
            width: 200,
            editable: false,
        },
        {
            field: 'status',
            headerName: 'Status',
            width: 200,
            editable: false,
        }
    ];

    const panes = [
        { menuItem: 'Patient Visit', render: () =>
                <Tab.Pane>
                   
                    <MaterialTable
                    title=""
                    columns={[
                        {
                            title: "Checked In Date",
                            field: "checkInDate", filtering: false 
                        },
                        { title: "Check Out Date", field: "checkOutDate", filtering: false  },
                        { title: "Service", field: "service", filtering: false  },
                        { title: "Status", field: "status", filtering: false },
                    ]}
                    isLoading={loading}
                    data={patientVisits.map((row) => ({
                        checkInDate: row.checkInDate,
                        checkOutDate: row.checkOutDate,
                        service:row.service,
                        status:(<Label color={row.status ==='COMPLETED' ? 'green' : 'red'} size="mini">{row.status}</Label>),

                    }))}

                    options={{
                        headerStyle: {
                            backgroundColor: "#000",
                            color: "#ffffff",
                        },
                        search: true,
                        // searchFieldStyle: {
                        //     width : '200%',
                        //     margingLeft: '250px',
                        // },
                        filtering: false,
                        exportButton: false,
                        searchFieldAlignment: 'left',
                        pageSizeOptions:[10,20,100],
                        pageSize:10,
                        debounceInterval: 400
                    }}
                />
                </Tab.Pane>
        },
        { menuItem: 'Appointments', render: () =>
                <Tab.Pane>
                    <div style={{ height: 400, width: '100%' }}>
                        <DataGrid
                            rows={appointments}
                            columns={appointmentColumns}
                            pageSize={5}
                            rowsPerPageOptions={[5]}
                            checkboxSelection
                            disableSelectionOnClick
                        />
                    </div>
                </Tab.Pane>
        }
    ];

    const handleCheckIn = () => {
        setModal(true);
    };
    const handleCheckOut = () => {
        setModalCheckOut(true);
    };

    const onCancelCheckIn = () => {
        setModal(false);
    };
    const onCancelCheckOut = () => {
        setModalCheckOut(false);
    };
    const onDelete = () => {

    };
    const handleInputChangeService = (e) => {
        setSelectedServices({ ...selectedServices, [e.target.name]: e.target.value });
    };
    //console.lo(selectedServices)

    const onSubmit = async (data) => {
        try {
            const today = new Date();
            const visitDetails = await axios.get(`${baseUrl}patient/visit/visit-detail/${patientObj.id}`, { headers: {"Authorization" : `Bearer ${token}`} });
            const visitDetail = visitDetails.data;
            const pendingVisit = visitDetail.find(obj => obj.status == "PENDING");
            let visit = null;
            if (!pendingVisit) {
                const visitResponse = await axios.post(`${baseUrl}patient/visit`, {
                    "personId": patientObj.id,
                    "visitStartDate": today
                }, { headers: {"Authorization" : `Bearer ${token}`} });
                visit = visitResponse.data;
            } else {
                visit = pendingVisit;
            }
            await axios.post(`${baseUrl}patient/encounter`, {
                "encounterDate": today,
                "personId": patientObj.id,
                "serviceCode": data.VisitType,
                "status": "PENDING",
                "visitId": visit.id
            }, { headers: {"Authorization" : `Bearer ${token}`} });
            setModal(false);
            await Swal.fire({
                icon: 'success',
                text: 'CheckedIn successfully',
                timer: 1500
            });
        } catch (e) {
            await Swal.fire({
                icon: 'error',
                title: 'Oops...',
                text: 'An error occurred while checking in Patient!',
            });
        }
    };
    const onError = async () => {
        await Swal.fire({
            icon: 'error',
            title: 'Oops...',
            text: 'An error occurred while checking in Patient!',
        });
    };

    let checkInServicesID = [];
    /**** Submit Button For CheckIN  */
    const handleSubmitCheckIn = (e) => {        
        e.preventDefault();              
        selectedServices.checkInServices.length > 0 && selectedServices.checkInServices.map((service)=> { 

            if(service.id!==null){
                checkInServicesID.push(service.id)
                console.log(service) 
            }
        })
        checkInObj.serviceIds= checkInServicesID  
        axios.post(`${baseUrl}patient/visit/checkin`, checkInObj,
        { headers: {"Authorization" : `Bearer ${token}`}},
        
        )
        .then(response => {
            toast.success("Patient Check-In successful");
            setCheckinStatus(true)
            onCancelCheckIn()
            loadPatientVisits()
        })
        .catch(error => {
            console.log(error)
            toast.error("Something went wrong");
            onCancelCheckIn()        
        });  
    }
    /**** Submit Button Processing  */
    const handleSubmitCheckOut = (e) => {        
        e.preventDefault();
        const getVisitID= patientVisits.find((visits)=> visits.status==='PENDING')   

        axios.put(`${baseUrl}patient/visit/checkout/${getVisitID.id}`,getVisitID.id,
        { headers: {"Authorization" : `Bearer ${token}`}},
        
        )
        .then(response => {
            toast.success("Record save successful");
            setCheckinStatus(false)
            onCancelCheckOut()
            loadPatientVisits()
        })
        .catch(error => {
            console.log(error)
            toast.error("Something went wrong");
            onCancelCheckOut()        
        });  
    }


    return (
        <div className={classes.root}>
            <Card>
                <CardContent>
                    
                    <PatientCardDetail patientObj={patientObj}/>
                    <Card>
                        <CardContent>
                            <div className="row">
                                <div className="mb-3 col-md-3">
                                    &nbsp;
                                </div>
                                <div className="mb-3 col-md-3">
                                    &nbsp;
                                </div>
                                <div className="mb-3 col-md-3">
                                    &nbsp;
                                </div>
                                <div className="mb-3 col-md-3">
                                <Link to={"/"} >
                                        <ButtonMui
                                            variant="contained"
                                            color="primary"
                                            className=" float-right mr-1">
                                            <span style={{ textTransform: "capitalize" }}>Back</span>
                                        </ButtonMui>
                                    </Link>
                                    {checkinStatus===false ? (
                                        <Button
                                            variant="contained"
                                            style={{ backgroundColor: "black" }}
                                            onClick={handleCheckIn}
                                            className=" float-right mr-1"
                                        >
                                            <span style={{ textTransform: "capitalize" }}>CheckIn</span>
                                        </Button>
                                    )
                                    :
                                    ""
                                    }
                                    {checkinStatus===true ? (
                                        <Button
                                            variant="contained"
                                            style={{ backgroundColor: "black" }}
                                            onClick={handleCheckOut}
                                            className=" float-right mr-1"
                                        >
                                            <span style={{ textTransform: "capitalize" }}>CheckOut</span>
                                        </Button>
                                    )
                                    :
                                    ""
                                    }
                                    
                                </div>
                            </div>
                            <Tab panes={panes} />
                        </CardContent>
                    </Card>
                </CardContent>
            </Card>
            <Modal isOpen={modal} toggle={onCancelCheckIn}>
                <ModalHeader toggle={onCancelCheckIn}>Select a Service Area</ModalHeader>
                <ModalBody>
                    <form onSubmit={handleSubmitCheckIn}>
                        <Paper
                            style={{
                                display: "grid",
                                gridRowGap: "20px",
                                padding: "20px",
                                margin: "10px 10px",
                            }}>
                            <Grid container spacing={2}>
                                <Grid item xs={8}>
                                    <FormControl >
                                       
                                         <Autocomplete
                                            multiple
                                            id="checkboxes-tags-demo"
                                            options={services}
                                            //disableCloseOnSelect
                                            getOptionLabel={(option) => option.moduleServiceName}
                                            onChange={(e, i) => {
                                                console.log(i)
                                                setSelectedServices({ ...selectedServices, checkInServices: i });
                                            }}
                                            renderOption={(props, option, { selected }) => (
                                                <li {...props}>
                                                <Checkbox
                                                    icon={icon}
                                                    checkedIcon={checkedIcon}
                                                    style={{ marginRight: 8 }}
                                                    checked={selected}
                                                />
                                                {option.moduleServiceName}
                                                </li>
                                            )}
                                            style={{ width: 400 }}
                                            renderInput={(params) => (
                                                <TextField {...params} label="Services" />
                                            )}
                                            />
                                       
                                    </FormControl>
                                </Grid>
                            </Grid>
                            <Grid container spacing={2}>
                                <Grid item xs={12}>
                                    <Button type={"submit"} variant="contained" color={"primary"}>Submit</Button>
                                </Grid>
                            </Grid>
                        </Paper>
                    </form>
                </ModalBody>
            </Modal>
            {/* Modal for CheckOut Patient */}
            <Modal isOpen={modalCheckOut} toggle={onCancelCheckOut}>
                <ModalHeader toggle={onCancelCheckOut}>CheckOut </ModalHeader>
                <ModalBody>
                    <form >
                        <Paper
                            style={{
                                display: "grid",
                                gridRowGap: "20px",
                                padding: "20px",
                                margin: "10px 10px",
                            }}>
                            <Grid container spacing={2}>
                                <Grid item xs={12}>
                                   <h5>Are you sure you want to check-out patient?</h5>
                                </Grid>
                            </Grid>
                            <Grid container spacing={2}>
                                <Grid item xs={12}>
                                    <Button type={"submit"} onClick={handleSubmitCheckOut} variant="contained" color={"primary"}>Yes</Button>
                                </Grid>
                            </Grid>
                        </Paper>
                    </form>
                </ModalBody>
            </Modal>
            {/* End of Checkout Modal */}
        </div>
    );
}

export default withStyles(styles)(PatientDashboard);
