import React, {useState, useEffect, useCallback} from 'react'
import MaterialTable from 'material-table';
import axios from "axios";
import { url as baseUrl, token } from "../../../api";
import { Link } from 'react-router-dom'
import { Card,CardBody,} from 'reactstrap';
import { Modal, ModalHeader, ModalBody, ModalFooter } from 'reactstrap';
import Button from "@material-ui/core/Button";
import 'react-toastify/dist/ReactToastify.css';
import 'react-widgets/dist/css/react-widgets.css';
import {FaEye, FaUserPlus} from "react-icons/fa";
import { MdDashboard, MdDeleteForever, MdModeEdit,MdPerson} from "react-icons/md";
import {Menu,MenuList,MenuButton,MenuItem,} from "@reach/menu-button";
import "@reach/menu-button/styles.css";
import { ToastContainer } from "react-toastify";
import { Label } from 'semantic-ui-react';
import { makeStyles } from '@material-ui/core/styles';
import "./patient.css";
import SplitActionButton from './SplitActionButton';

import { forwardRef } from 'react';
//import { Button} from "react-bootstrap";
import AddBox from '@material-ui/icons/AddBox';
import ArrowUpward from '@material-ui/icons/ArrowUpward';
import Check from '@material-ui/icons/Check';
import ChevronLeft from '@material-ui/icons/ChevronLeft';
import ChevronRight from '@material-ui/icons/ChevronRight';
import Clear from '@material-ui/icons/Clear';
import DeleteOutline from '@material-ui/icons/DeleteOutline';
import Edit from '@material-ui/icons/Edit';
import FilterList from '@material-ui/icons/FilterList';
import FirstPage from '@material-ui/icons/FirstPage';
import LastPage from '@material-ui/icons/LastPage';
import Remove from '@material-ui/icons/Remove';
import SaveAlt from '@material-ui/icons/SaveAlt';
import Search from '@material-ui/icons/Search';
import ViewColumn from '@material-ui/icons/ViewColumn';

const tableIcons = {
    Add: forwardRef((props, ref) => <AddBox {...props} ref={ref} />),
Check: forwardRef((props, ref) => <Check {...props} ref={ref} />),
Clear: forwardRef((props, ref) => <Clear {...props} ref={ref} />),
Delete: forwardRef((props, ref) => <DeleteOutline {...props} ref={ref} />),
DetailPanel: forwardRef((props, ref) => <ChevronRight {...props} ref={ref} />),
Edit: forwardRef((props, ref) => <Edit {...props} ref={ref} />),
Export: forwardRef((props, ref) => <SaveAlt {...props} ref={ref} />),
Filter: forwardRef((props, ref) => <FilterList {...props} ref={ref} />),
FirstPage: forwardRef((props, ref) => <FirstPage {...props} ref={ref} />),
LastPage: forwardRef((props, ref) => <LastPage {...props} ref={ref} />),
NextPage: forwardRef((props, ref) => <ChevronRight {...props} ref={ref} />),
PreviousPage: forwardRef((props, ref) => <ChevronLeft {...props} ref={ref} />),
ResetSearch: forwardRef((props, ref) => <Clear {...props} ref={ref} />),
Search: forwardRef((props, ref) => <Search {...props} ref={ref} />),
SortArrow: forwardRef((props, ref) => <ArrowUpward {...props} ref={ref} />),
ThirdStateCheck: forwardRef((props, ref) => <Remove {...props} ref={ref} />),
ViewColumn: forwardRef((props, ref) => <ViewColumn {...props} ref={ref} />)
};


const useStyles = makeStyles(theme => ({
    card: {
        margin: theme.spacing(20),
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center'
    },
    form: {
        width: '100%', // Fix IE 11 issue.
        marginTop: theme.spacing(3)
    },
    submit: {
        margin: theme.spacing(3, 0, 2)
    },
    cardBottom: {
        marginBottom: 20
    },
    Select: {
        height: 45,
        width: 350
    },
    button: {
        margin: theme.spacing(1)
    },

    root: {
        '& > *': {
            margin: theme.spacing(1)
        }
    },
    input: {
        display: 'none'
    },
    error: {
        color: "#f85032",
        fontSize: "11px",
    },
    success: {
        color: "#4BB543 ",
        fontSize: "11px",
    },
}));



const PatientList = (props) => {

    const [patients, setPatients] = useState([]);
    const [permissions, setPermissions] = useState([]);
    const [loading, setLoading] = useState('');
    const [modal, setModal] = useState(false);
    const [patient, setPatient] = useState(false);
    const toggle = (id) => {
        const patient = patients.find(obj => obj.id == id);
        setPatient(patient);
        setModal(!modal);
    }
    useEffect(() => {
        userPermission();
      }, []);
    //Get list of Finger index
    const userPermission =()=>{
        axios
           .get(`${baseUrl}account`,
               { headers: {"Authorization" : `Bearer ${token}`} }
           )
           .then((response) => {
                setPermissions(response.data.permissions);

           })
           .catch((error) => {
           });

     }
    const loadPatients = useCallback(async () => {
        try {
            const response = await axios.get(`${baseUrl}patient`, { headers: {"Authorization" : `Bearer ${token}`} });
            setPatients(response.data);
        } catch (e) {
            console.log(e);
        }
    }, []);

    const onDelete = async (id) => {
        try {
            if (id) {
                const response = await axios.delete(`${baseUrl}patient/${id}`, { headers: {"Authorization" : `Bearer ${token}`} });
                window.location.reload();
            }
        } catch (e) {

        }
    }

    const onCancelDelete = () => {
        setModal(false);
    }

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

    const getHospitalNumber = (identifier) => {
        const hospitalNumber = identifier.identifier.find(obj => obj.type == 'HospitalNumber');
        return hospitalNumber ? hospitalNumber.value : '';
    };

    const getAddress = (address) => {
        const city = address && address.address && address.address.length > 0 ? address.address[0].city : null;
        return city;
    };

    const getGender = (gender) => {
        return gender.display;
    };

    useEffect(() => {
        loadPatients();
    }, [loadPatients]);
    console.log(permissions)
console.log(permissions.includes( "all_permission"))
    function actionItems(row){
       return  [
           {
               name:'View',
               type:'link',
               icon:<FaEye  size="22"/>,
               to:{
                   pathname: "/register-patient",
                   state: { patientId : row.id, permissions:permissions  }
               }
           },
           {...(permissions.includes('view_patient') || permissions.includes("all_permission")&&
                   {
                       name:'Patient Dashboard',
                       type:'link',
                       icon:<MdPerson size="20" color='rgb(4, 196, 217)' />,
                       to:{
                           pathname: "/patient-dashboard",
                           state: { patientObj: row, permissions:permissions  }
                       }
                   }
           )},
           {...(permissions.includes('edit_patient') || permissions.includes("all_permission")&&
                   {
                       name:'Edit Patient',
                       type:'link',
                       icon:<MdModeEdit size="20" color='rgb(4, 196, 217)' />,
                       to:{
                           pathname: "/register-patient",
                           state: { patientId : row.id, permissions:permissions  }
                       }
                   }
               )},
           {...(permissions.includes('delete_patient') || permissions.includes("all_permission")&&
                   {
                       name:'Delete Patient',
                       type:'link',
                       icon:<MdDeleteForever size="20" color='rgb(4, 196, 217)'  />,
                       to:{
                           pathname: "/#",
                           state: { patientObj: row, permissions:permissions  }
                       }
                   }
               )}
        ]
    }
  return (
    <div>
        <ToastContainer autoClose={3000} hideProgressBar />
        <Card>
            <CardBody>
            {permissions.includes('view_patient') || permissions.includes("all_permission") ? (
                <Link to={"register-patient"}>
                    <Button
                        variant="contained"
                        color="primary"
                        className=" float-right mr-1"
                        startIcon={<FaUserPlus size="25"/>}
                        style={{backgroundColor:'rgb(4, 196, 217)'}}
                    >
                        <span style={{ textTransform: "capitalize", fontWeight:'bolder' }}>New Patient</span>
                    </Button>
                </Link>
            ):""
        }
                <br/><br/>
                <br/>
                <MaterialTable
                    icons={tableIcons}
                    title="Find patients"
                    columns={[
                        {
                            title: "Patient Name",
                            field: "name", filtering: false
                        },
                        { title: "Patient ID", field: "id" , filtering: false},
                        { title: "Gender", field: "gender", filtering: false },
                        { title: "Date Of Birth", field: "dateOfBirth", filtering: false },
                        { title: "Age", field: "age", filtering: false },
                        /*{ title: "Address", field: "address", filtering: false },*/
                        { title: "Status", field: "status", filtering: false },
                        {title: "Actions", field: "actions", filtering: false },
                    ]}
                    isLoading={loading}
                    data={patients.map((row) => ({
                        name: row.firstName +  ' ' + row.otherName +  ' ' + row.surname,
                        id: getHospitalNumber(row.identifier),
                        gender: getGender(row.gender),
                        dateOfBirth: row.dateOfBirth,
                        age: (row.dateOfBirth === 0 ||
                            row.dateOfBirth === undefined ||
                            row.dateOfBirth === null ||
                            row.dateOfBirth === "" )
                            ? 0
                            : calculate_age(row.dateOfBirth),
                        /*address: getAddress(row.address),*/
/*                        status: row.active ?
                            (<Label color="green" size="mini">active</Label>)
                            : (<Label color="teal" size="mini">not-active</Label>),*/
                        status: row.active ?
                            "Active"
                            : "Not-Active",
                        actions:
                            <div>
                                {permissions.includes('view_patient') || permissions.includes("all_permission") ? (
                                    <SplitActionButton actions={actionItems(row)} />
                                ):""
                                }

{/*                                <Menu>
                                    <MenuButton style={{ backgroundColor:"#3F51B5", color:"#fff", border:"2px solid #3F51B5", borderRadius:"4px", }}>
                                        Actions <span aria-hidden>▾</span>
                                    </MenuButton>

                                    <MenuList className={'menuClass'} >
                                    {permissions.includes('view_patient') || permissions.includes("all_permission") ? (
                                        <MenuItem  style={{ color:"#000 !important"}}>
                                            <Link
                                                to={{
                                                    pathname: "/patient-dashboard",
                                                    state: { patientObj: row, permissions:permissions }
                                                }}
                                            >
                                                <MdDashboard size="15" />{" "}<span style={{color: '#000'}}>Patient Dashboard</span>
                                            </Link>
                                        </MenuItem>
                                    ):""
                                }

                                    {permissions.includes('edit_patient') || permissions.includes("all_permission") ? (
                                        <MenuItem style={{ color:"#000 !important"}}>
                                            <Link
                                                to={{
                                                    pathname: "/register-patient",
                                                    state: { patientId : row.id }
                                                }}
                                            >
                                                <MdModeEdit size="15" />{" "}<span style={{color: '#000'}}>Edit Patient </span>
                                            </Link>
                                        </MenuItem>
                                    ):""}
                                    {permissions.includes('delete_patient') || permissions.includes("all_permission") ? (
                                        <MenuItem style={{ color:"#000 !important"}}>
                                            <Link
                                                onClick={(e) => toggle(row.id)}
                                                to={{
                                                    pathname: "/#",
                                                    currentId: row
                                                }}
                                            >
                                                <MdDeleteForever size="15"  />{" "}
                                                <span style={{color: '#000'}}>Delete Patient</span>
                                            </Link>
                                        </MenuItem>
                                    ):""}
                                    </MenuList>
                                </Menu>*/}
                            </div>
                    }))}

                    options={{
                        headerStyle: {
                            //backgroundColor: "#9F9FA5",
                            color: "#000",
                        },
                        searchFieldStyle: {
                            width : '200%',
                            margingLeft: '250px',
                        },
                        filtering: true,
                        exportButton: false,
                        searchFieldAlignment: 'left',
                        pageSizeOptions:[10,20,100],
                        pageSize:10,
                        debounceInterval: 400
                    }}
                />

            </CardBody>
        </Card>
        <Modal isOpen={modal} toggle={onCancelDelete}>
            <ModalHeader toggle={onCancelDelete}>Delete Patient</ModalHeader>
            <ModalBody>
                Are you sure to delete this record? { patient ? patient.surname +  ', ' + patient.firstname +  ' ' + patient.otherName : '' }
            </ModalBody>
            <ModalFooter>
                <Button color="primary" type="button" onClick={(e) => onDelete(patient.id)}>Yes</Button>{' '}
                <Button color="secondary" type="button" onClick={(e) => onCancelDelete()}>No</Button>
            </ModalFooter>
        </Modal>
    </div>
  );
}

export default PatientList;


