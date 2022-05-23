import React, {useCallback, useEffect, useState} from 'react';
import PropTypes from 'prop-types';
import { withStyles } from '@material-ui/core/styles';
import { Link } from 'react-router-dom'
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

const columns = [
    {
        field: 'checkedInDate',
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
    { id: 2, lastName: 'Lannister', firstName: 'Cersei', age: 42 },
    { id: 3, lastName: 'Lannister', firstName: 'Jaime', age: 45 },
    { id: 4, lastName: 'Stark', firstName: 'Arya', age: 16 },
    { id: 5, lastName: 'Targaryen', firstName: 'Daenerys', age: null },
    { id: 6, lastName: 'Melisandre', firstName: null, age: 150 },
    { id: 7, lastName: 'Clifford', firstName: 'Ferrara', age: 44 },
    { id: 8, lastName: 'Frances', firstName: 'Rossini', age: 36 },
    { id: 9, lastName: 'Roxie', firstName: 'Harvey', age: 65 },
];

function PatientDashboard(props) {
    let history = useHistory();
    const { classes } = props;
    const patientObj = history.location && history.location.state ? history.location.state.patientObj : {};
    const { handleSubmit, control } = useForm();
    const [modal, setModal] = useState(false);
    const [services, setServices]= useState([]);
    console.log(patientObj);

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

    useEffect(() => {
        loadServices();
    }, [loadServices]);

    let visitTypesRows = null;
    if (services && services.length > 0) {
        visitTypesRows = services.map((service, index) => (
            <MenuItem key={service.moduleServiceCode} value={service.moduleServiceCode}>{service.moduleServiceName}</MenuItem>
        ));
    }

    const rows = [];
    const panes = [
        { menuItem: 'Patient Visit', render: () =>
                <Tab.Pane>
                    <div style={{ height: 400, width: '100%' }}>
                        <DataGrid
                            rows={rows}
                            columns={columns}
                            pageSize={5}
                            rowsPerPageOptions={[5]}
                            disableSelectionOnClick
                        />
                    </div>
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

    const onCancelCheckIn = () => {
        setModal(false);
    };

    const onDelete = () => {

    };

    const onSubmit = async (data) => {
        try {
            const today = new Date();
            const visit = await axios.post(`${baseUrl}patient/visit`, {
                "personId": patientObj.id,
                "visitStartDate": today
            }, { headers: {"Authorization" : `Bearer ${token}`} });
            await axios.post(`${baseUrl}patient/encounter`, {
                "encounterDate": today,
                "personId": patientObj.id,
                "serviceCode": data.VisitType,
                "status": "PENDING",
                "visitId": visit.id
            }, { headers: {"Authorization" : `Bearer ${token}`} });
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

    return (
        <div className={classes.root}>
            <Card>
                <CardContent>
                    <Link to={"/"} >
                        <ButtonMui
                            variant="contained"
                            color="primary"
                            className=" float-right mr-1">
                            <span style={{ textTransform: "capitalize" }}>Back</span>
                        </ButtonMui>
                    </Link>
                    <br/><br/>
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
                                    <Box
                                        m={1}
                                        display="flex"
                                        justifyContent="center"
                                        alignItems="center"
                                    >
                                        <Button
                                            variant="contained"
                                            style={{ backgroundColor: "black" }}
                                            onClick={handleCheckIn}
                                        >
                                            CheckIn
                                        </Button>
                                    </Box>
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
                    <form onSubmit={handleSubmit(onSubmit, onError)}>
                        <Paper
                            style={{
                                display: "grid",
                                gridRowGap: "20px",
                                padding: "20px",
                                margin: "10px 10px",
                            }}>
                            <Grid container spacing={2}>
                                <Grid item xs={4}>
                                    <FormControl fullWidth>
                                        <Controller
                                            name="VisitType"
                                            control={control}
                                            defaultValue=""
                                            render={({ field, fieldState: { error}}) => (
                                                <TextField
                                                    {...field}
                                                    label="Visit Type"
                                                    id="VisitType"
                                                    variant="outlined"
                                                    select
                                                    error={!!error}
                                                    helperText={error ? error.message : null}
                                                >
                                                    <MenuItem></MenuItem>
                                                    {visitTypesRows}
                                                </TextField>
                                            )}
                                            rules={{ required: 'Visit Type is Required' }}
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
        </div>
    );
}

export default withStyles(styles)(PatientDashboard);
