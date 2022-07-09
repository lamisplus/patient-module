import React, {useCallback, useEffect, useState} from "react";
import axios from "axios";
import {format} from 'date-fns';
import MatButton from "@material-ui/core/Button";
import Button from "@material-ui/core/Button";
import {Form, FormGroup, Label, Spinner,} from "reactstrap";
import {FontAwesomeIcon} from '@fortawesome/react-fontawesome';
import {library} from '@fortawesome/fontawesome-svg-core'
import {faCheckSquare, faCoffee, faEdit, faTrash} from '@fortawesome/free-solid-svg-icons'
import {yupResolver} from '@hookform/resolvers/yup';
import * as yup from "yup";
import * as moment from 'moment';
import {makeStyles} from "@material-ui/core/styles";
import {Card, CardContent} from "@material-ui/core";
import SaveIcon from "@material-ui/icons/Save";
import AddIcon from "@material-ui/icons/Add";
import CancelIcon from "@material-ui/icons/Cancel";
import {ToastContainer, toast} from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import "react-widgets/dist/css/react-widgets.css";
import {Link, useHistory, useLocation} from "react-router-dom";
import {TiArrowBack} from 'react-icons/ti'
import {useForm} from "react-hook-form";
import {token, url as baseUrl } from "../../../api";


library.add(faCheckSquare, faCoffee, faEdit, faTrash);

const useStyles = makeStyles((theme) => ({
    card: {
        margin: theme.spacing(20),
        display: "flex",
        flexDirection: "column",
        alignItems: "center"
    },
    form: {
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
        width: 300,
    },
    button: {
        margin: theme.spacing(1),
    },
    root: {
        flexGrow: 1,
        maxWidth: 752,
    },
    demo: {
        backgroundColor: theme.palette.background.default,
    },
    inline: {
        display: "inline",
    },
}));

const schema = yup.object().shape({
    dateOfRegistration: yup.date().required(),
    hospitalNumber: yup.string().required(),
    firstName: yup.string().required(),
    middleName: yup.string().nullable(),
    lastName: yup.string().required(),
    sex: yup.number().required(),
    employmentStatus: yup.number().required(),
    highestQualification: yup.number().required(),
    maritalStatus: yup.number().required(),
    dob: yup.date().required(),
    dateOfBirth: yup.string().required(),
    age: yup.number(),
    pnumber: yup.string().required(),
    altPhonenumber: yup.string().nullable(),
    email: yup.string().nullable(),
    address: yup.string().nullable(),
    landmark: yup.string().nullable(),
    countryId: yup.number().required(),
    stateId: yup.number().required(),
    district: yup.number().nullable(),
});

const UserRegistration = (props) => {
    const { register, setValue, getValues, setError, handleSubmit, formState: { errors } } = useForm({
        resolver: yupResolver(schema),
    });
    const [today, setToday] = useState(new Date().toISOString().substr(0, 10).replace('T', ' '));
    const [contacts, setContacts] = useState([]);
    const [saving, setSaving] = useState(false);
    const [ageDisabled, setAgeDisabled] = useState(true);
    const [showRelative, setShowRelative] = useState(false);
    const [editRelative, setEditRelative] = useState(null);
    const [genders, setGenders]= useState([]);
    const [maritalStatusOptions, setMaritalStatusOptions]= useState([]);
    const [educationOptions, setEducationOptions]= useState([]);
    const [occupationOptions, setOccupationOptions]= useState([]);
    const [relationshipOptions, setRelationshipOptions]= useState([]);
    const [topLevelUnitCountryOptions, settopLevelUnitCountryOptions]= useState([]);
    const [stateUnitOptions, setStateUnitOptions]= useState([]);
    const [districtUnitOptions, setDistrictUnitOptions]= useState([]);
    const userDetail = props.location && props.location.state ? props.location.state.user : null;
    const classes = useStyles();
    const history = useHistory();

    const location = useLocation();
    const locationState = location.state;
    let patientId = null;
    patientId = locationState ? locationState.patientId : null;

    const getNames = (relationship) => {
        const surname = relationship.surname;
        const firstName = relationship.firstname;
        const otherName = relationship.otherName ? relationship.otherName : '';
        return surname + ', ' + firstName + ' ' + otherName;
    }
    const getRelationship = (relationshipId) => {
        const relationship = relationshipOptions.find(obj => obj.id == relationshipId);
        return relationship ? relationship.display : '';
    };
    const getPhoneContactPoint = (contactPoint) => {
        return contactPoint ? contactPoint.value : '';
    };
    const getAddress = (address) => {
        return address && address.line && address.line.length > 0 ? address.line[0] : '';
    };
    const getPatient = useCallback(async () => {
        if (patientId) {
            const response = await axios.get(`${baseUrl}patient/${patientId}`, { headers: {"Authorization" : `Bearer ${token}`} });
            const patient = response.data;
            const contacts = patient.contact ? patient.contact : [];
            setContacts(contacts.contact);
            const identifiers = patient.identifier;
            const address = patient.address;
            const contactPoint = patient.contactPoint;
            const hospitalNumber = identifiers.identifier.find(obj => obj.type == 'HospitalNumber');
            const phone = contactPoint.contactPoint.find(obj => obj.type == 'phone');
            const email = contactPoint.contactPoint.find(obj => obj.type == 'email');
            const altphone = contactPoint.contactPoint.find(obj => obj.type == 'altphone');
            const country = address && address.address && address.address.length > 0 ? address.address[0] : null;
            const gender = patient.gender;
            const employmentStatus = patient.employmentStatus;
            const education = patient.education;
            const maritalStatus = patient.maritalStatus;
            setValue('dateOfRegistration', patient.dateOfRegistration);
            setValue('firstName', patient.firstName);
            setValue('middleName', patient.otherName);
            setValue('lastName', patient.surname);
            setValue('hospitalNumber', hospitalNumber ? hospitalNumber.value : '');
            setValue('maritalStatus', maritalStatus.id);
            setValue('employmentStatus', employmentStatus.id);
            setValue('sex', gender.id);
            setValue('highestQualification', education.id);
            setValue('dob', format(new Date(patient.dateOfBirth), 'yyyy-MM-dd'));
            if (country) {
                setValue('countryId', country.countryId);
                const stateOptions = await loadOrganisationUnitsByParentId(country.countryId);
                setStateUnitOptions(stateOptions);
                setValue('stateId', country.stateId);
                const districtOptions = await loadOrganisationUnitsByParentId(country.stateId);
                setDistrictUnitOptions(districtOptions);
                setValue('district', country.district);
                setValue('address', country.city);
                setValue('landmark', country.line[0]);
            }
            setValue('pnumber', phone ? phone.value : null);
            setValue('email', email ? email.value : null);
            setValue('altPhonenumber', altphone ? altphone.value : null);
        }
    }, []);
    const handleAddRelative = () => {
        setShowRelative(true);
    };
    const handleSaveRelationship = (e) => {
        const relationshipType = getValues("relationshipType");
        const cfirstName = getValues("cfirstName");
        const cmiddleName = getValues("cmiddleName");
        const clastName = getValues("clastName");
        const contactPhoneNumber = getValues("contactPhoneNumber");
        const contactEmail = getValues("contactEmail");
        const contactAddress = getValues("contactAddress");
        if (!relationshipType) {
            setError("relationshipType", {  type: 'custom', message: 'Relationship is required' }, { shouldFocus: true });
            return;
        }
        if (!cfirstName) {
            setError("cfirstName", {  type: 'custom', message: 'FirstName is required' }, { shouldFocus: true });
            return;
        }
        if (!clastName) {
            setError("clastName", {  type: 'custom', message: 'LastName is required' }, { shouldFocus: true });
            return;
        }

        const contact = {
            "address": {
                "line": [
                    contactAddress
                ],
            },
            "contactPoint": {
                "type": "phone",
                "value": contactPhoneNumber
            },
            "firstName": cfirstName,
            "fullName": cfirstName + ' ' + cmiddleName + ' ' + clastName,
            "relationshipId": relationshipType,
            "surname": clastName,
            "otherName": cmiddleName
        };
        if (editRelative != null) {
            contacts.splice(editRelative, 1);
            setContacts(contacts.concat(contact));
        } else {
            setContacts(contacts.concat(contact));
        }
        setShowRelative(false);
    };
    const handleCancelSaveRelationship = () => {
        setShowRelative(false);
    }
    const onSubmit = async (data) => {
        try {
            const patientForm = {
                active: true,
                address: [
                    {
                        "city": data.address,
                        "countryId": data.countryId,
                        "district": data.district,
                        "line": [
                            data.landmark
                        ],
                        "organisationUnitId": 0,
                        "postalCode": "",
                        "stateId": data.stateId
                    }
                ],
                contact: contacts,
                contactPoint: [],
                dateOfBirth: new Date(data.dob),
                deceased: false,
                deceasedDateTime: null,
                firstName: data.firstName,
                genderId: data.sex,
                identifier: [
                    {
                        "assignerId": 1,
                        "type": "HospitalNumber",
                        "value": data.hospitalNumber
                    }
                ],
                otherName: data.middleName,
                maritalStatusId: data.maritalStatus,
                surname: data.lastName,
                educationId: data.highestQualification,
                employmentStatusId: data.employmentStatus,
                dateOfRegistration: data.dateOfRegistration,
                isDateOfBirthEstimated: data.dateOfBirth == "Actual" ? false : true
            };
            const phone = {
                "type": "phone",
                "value": data.pnumber
            };
            if (data.email) {
                const email = {
                    "type": "email",
                    "value": data.email
                }
                patientForm.contactPoint.push(email);
            }
            if (data.altPhonenumber) {
                const altPhonenumber = {
                    "type": "altphone",
                    "value": data.altPhonenumber
                }
                patientForm.contactPoint.push(altPhonenumber);
            }
            patientForm.contactPoint.push(phone);
            if (patientId) {
                patientForm.id = null;
                const response = await axios.put(`${baseUrl}patient/${patientId}`, patientForm, { headers: {"Authorization" : `Bearer ${token}`} });
            } else {
                const response = await axios.post(`${baseUrl}patient`, patientForm, { headers: {"Authorization" : `Bearer ${token}`} });
            }
            toast.success("Patient Register successful");
            history.push('/');
        } catch (e) {
            console.log(e);
            toast.error("An error occured while registering a patient !", {
                position: toast.POSITION.TOP_RIGHT
            });
        }
    };
    const onError = (errors) => {
        console.error(errors)
    }
    const handleEditRelative = (relative, index) => {
        setValue("relationshipType", relative.relationshipId);
        setValue("cfirstName", relative.firstname);
        setValue("cmiddleName", relative.otherName);
        setValue("clastName", relative.surname);
        setValue("contactPhoneNumber", relative.contactPoint ? relative.contactPoint.value : '');
        setValue("contactAddress", relative.address && relative.address.line && relative.address.line.length > 0 ? relative.address.line[0] : '');
        setShowRelative(true);
        setEditRelative(index);
    };
    const handleDeleteRelative = (index) => {
        setTimeout(() => {
            contacts.splice(index, 1);
            setContacts(contacts);
            setShowRelative(true);
            setShowRelative(false);
        }, 500);
    };

    const loadGenders = useCallback(async () => {
        try {
            const response = await axios.get(`${baseUrl}application-codesets/v2/GENDER`, { headers: {"Authorization" : `Bearer ${token}`} });
            setGenders(response.data);
        } catch (e) {
            toast.error("An error occured while fetching gender codesets !", {
                position: toast.POSITION.TOP_RIGHT
            });
        }
    }, []);
    const loadMaritalStatus = useCallback(async () => {
        try {
            const response = await axios.get(`${baseUrl}application-codesets/v2/MARITAL_STATUS`, { headers: {"Authorization" : `Bearer ${token}`} });
            setMaritalStatusOptions(response.data);
        } catch (e) {
            toast.error("An error occured while fetching marital codesets !", {
                position: toast.POSITION.TOP_RIGHT
            });
        }
    }, []);
    const loadEducation = useCallback(async () => {
        try {
            const response = await axios.get(`${baseUrl}application-codesets/v2/EDUCATION`, { headers: {"Authorization" : `Bearer ${token}`} });
            setEducationOptions(response.data);
        } catch (e) {
            toast.error("An error occured while fetching education codesets !", {
                position: toast.POSITION.TOP_RIGHT
            });
        }
    }, []);
    const loadOccupation = useCallback(async () => {
        try {
            const response = await axios.get(`${baseUrl}application-codesets/v2/OCCUPATION`, { headers: {"Authorization" : `Bearer ${token}`} });
            setOccupationOptions(response.data);
        } catch (e) {
            toast.error("An error occured while fetching occupation codesets !", {
                position: toast.POSITION.TOP_RIGHT
            });
        }
    }, []);
    const loadRelationships = useCallback(async () => {
      try {
          const response = await axios.get(`${baseUrl}application-codesets/v2/RELATIONSHIP`, { headers: {"Authorization" : `Bearer ${token}`} });
          setRelationshipOptions(response.data);
      } catch (e) {
          toast.error("An error occured while fetching relationship codesets !", {
              position: toast.POSITION.TOP_RIGHT
          });
      }
    }, []);
    const loadTopLevelCountry = useCallback(async () => {
        const response = await axios.get(`${baseUrl}organisation-units/parent-organisation-units/0`, { headers: {"Authorization" : `Bearer ${token}`} });
        settopLevelUnitCountryOptions(response.data);
    }, []);
    const loadOrganisationUnitsByParentId = async (parentId) => {
        const response = await axios.get(`${baseUrl}organisation-units/parent-organisation-units/${parentId}`, { headers: {"Authorization" : `Bearer ${token}`} });
        return response.data;
    };
    const onCountryChange = async (e) => {
        if (e.target.value) {
            const stateOptions = await loadOrganisationUnitsByParentId(e.target.value);
            setStateUnitOptions(stateOptions);
        } else {
            setStateUnitOptions([]);
        }
    };
    const onStateChange = async (e) => {
        if (e.target.value) {
            const districtOptions = await loadOrganisationUnitsByParentId(e.target.value);
            setDistrictUnitOptions(districtOptions);
        } else {
            setDistrictUnitOptions([]);
        }
    };
    
    const handleDobChange = (e) => {
        if (e.target.value) {
            const today = new Date();
            const birthDate = new Date(e.target.value);
            let age_now = today.getFullYear() - birthDate.getFullYear();
            const m = today.getMonth() - birthDate.getMonth();
            if (m < 0 || (m === 0 && today.getDate() < birthDate.getDate())) {
                age_now--;
            }
            setValue('age', age_now);
        } else {
            setValue('age', null);
        }
    }

    const handleDateOfBirthChange = (e) => {
        if (e.target.value == "Actual") {
            setAgeDisabled(true);
        } else if (e.target.value == "Estimated") {
            setAgeDisabled(false);
        }
    }

    const handleAgeChange = (e) => {
        if (!ageDisabled && e.target.value) {
            const currentDate = new Date();
            currentDate.setDate(15);
            currentDate.setMonth(5);
            const estDob = moment(currentDate.toISOString());
            const dob = estDob.add((e.target.value * -1), 'years');
            setValue('dob', format(new Date(dob.toDate()), 'yyyy-MM-dd'));
        }
    }

    useEffect(() => {
        loadGenders();
        loadMaritalStatus();
        loadEducation();
        loadOccupation();
        loadRelationships();
        loadTopLevelCountry();
        getPatient();
    }, [loadGenders, loadMaritalStatus, loadEducation, loadOccupation, loadRelationships, loadTopLevelCountry, getPatient]);

    let genderRows = null;
    let maritalStatusRows = null;
    let educationRows = null;
    let occupationRows = null;
    let relationshipRows = null;
    let topLevelUnitCountryRows = null;
    let stateRows = null;
    let districtRows = null;
    if (genders && genders.length > 0) {
        genderRows = genders.map((gender, index) => (
            <option key={gender.id} value={gender.id}>{gender.display}</option>
        ));
    }
    if (maritalStatusOptions && maritalStatusOptions.length > 0) {
        maritalStatusRows = maritalStatusOptions.map((maritalStatusOption, index) => (
            <option key={maritalStatusOption.id} value={maritalStatusOption.id}>{maritalStatusOption.display}</option>
        ));
    }
    if (educationOptions && educationOptions.length > 0) {
        educationRows = educationOptions.map((educationOption, index) => (
            <option key={educationOption.id} value={educationOption.id}>{educationOption.display}</option>
        ));
    }
    if (occupationOptions && occupationOptions.length > 0) {
        occupationRows = occupationOptions.map((occupationOption, index) => (
            <option key={occupationOption.id} value={occupationOption.id}>{occupationOption.display}</option>
        ));
    }
    if (relationshipOptions && relationshipOptions.length > 0) {
        relationshipRows = relationshipOptions.map((relationshipOption, index) => (
            <option key={relationshipOption.id} value={relationshipOption.id}>{relationshipOption.display}</option>
        ));
    }
    if (topLevelUnitCountryOptions && topLevelUnitCountryOptions.length > 0) {
        topLevelUnitCountryRows = topLevelUnitCountryOptions.map((topLevelUnitCountryOption, index) => (
            <option key={topLevelUnitCountryOption.id} value={topLevelUnitCountryOption.id}>{topLevelUnitCountryOption.name}</option>
        ));
    }
    if (stateUnitOptions && stateUnitOptions.length > 0) {
        stateRows = stateUnitOptions.map((stateUnitOption, index) => (
            <option key={stateUnitOption.id} value={stateUnitOption.id}>{stateUnitOption.name}</option>
        ));
    }
    if (districtUnitOptions && districtUnitOptions.length > 0) {
        districtRows = districtUnitOptions.map((districtUnitOption, index) => (
            <option key={districtUnitOption.id} value={districtUnitOption.id}>{districtUnitOption.name}</option>
        ));
    }

    const handleCancel = () => {
        history.push('/');
    };

    return (
        <>
            <ToastContainer autoClose={3000} hideProgressBar />
            <Card className={classes.cardBottom}>
                <CardContent>
                    <Link
                        to={{
                            pathname: "/",
                            state: 'users'
                        }}>
                        <Button
                            variant="contained"
                            color="primary"
                            className=" float-right ms-1"
                            style={{backgroundColor:'#04C4D9',fontWeight:"bolder"}}
                            startIcon={<TiArrowBack />}
                        >
                            <span style={{ textTransform: "capitalize" }}>Back </span>
                        </Button>
                    </Link>
                    <br />
                    <br />
                    <div className="col-xl-12 col-lg-12">
                        <Form onSubmit={handleSubmit(onSubmit, onError)}>
                            <div className="card">
                                <div className="card-header" style={{backgroundColor:"#04C4D9",color:'#fff',fontWeight:'bolder'}}>
                                    <h5 className="card-title">{userDetail===null ? "Basic Information" : "Edit User Information"}</h5>
                                </div>

                                <div className="card-body">
                                    <div className="basic-form">
                                        <div className="row">
                                            <div className="form-group mb-3 col-md-4">
                                                <FormGroup>
                                                    <Label for="dateOfRegistration">Date of Registration*</Label>
                                                    <input
                                                        className="form-control"
                                                        type="date"
                                                        name="dateOfRegistration"
                                                        id="dateOfRegistration"
                                                        max={today}
                                                        {...register("dateOfRegistration")}
                                                        style={{border: "1px solid #04C4D9"}}
                                                    />
                                                    {errors.dateOfRegistration && <p>{errors.dateOfRegistration.message}</p>}
                                                </FormGroup>
                                            </div>

                                            <div className="form-group mb-3 col-md-4">
                                                <FormGroup>
                                                    <Label for="patientId">Hospital Number* </Label>
                                                    <input
                                                        className="form-control"
                                                        type="text"
                                                        name="hospitalNumber"
                                                        id="hospitalNumber"
                                                        {...register("hospitalNumber")}
                                                        style={{border: "1px solid #04C4D9"}}
                                                    />
                                                    {errors.hospitalNumber && <p>{errors.hospitalNumber.message}</p>}
                                                </FormGroup>
                                            </div>
                                        </div>

                                        <div className="row">
                                            <div className="form-group mb-3 col-md-4">
                                                <FormGroup>
                                                    <Label for="firstName">First Name *</Label>
                                                    <input
                                                        className="form-control"
                                                        type="text"
                                                        name="firstName"
                                                        id="firstName"
                                                        {...register("firstName")}
                                                        style={{border: "1px solid #04C4D9"}}
                                                    />
                                                    {errors.firstName && <p>{errors.firstName.message}</p>}
                                                </FormGroup>
                                            </div>

                                            <div className="form-group mb-3 col-md-4">
                                                <FormGroup>
                                                    <Label>Middle Name</Label>
                                                    <input
                                                        className="form-control"
                                                        type="text"
                                                        name="middleName"
                                                        id="middleName"
                                                        {...register("middleName")}
                                                        style={{border: "1px solid #04C4D9"}}
                                                    />
                                                    {errors.middleName && <p>{errors.middleName.message}</p>}
                                                </FormGroup>
                                            </div>

                                            <div className="form-group mb-3 col-md-4">
                                                <FormGroup>
                                                    <Label>Last Name *</Label>
                                                    <input
                                                        className="form-control"
                                                        type="text"
                                                        name="lastName"
                                                        id="lastName"
                                                        {...register("lastName")}
                                                        style={{border: "1px solid #04C4D9"}}
                                                    />
                                                    {errors.lastName && <p>{errors.lastName.message}</p>}
                                                </FormGroup>
                                            </div>
                                        </div>

                                        <div className="row">
                                            <div className="form-group  col-md-4">
                                                <FormGroup>
                                                    <Label>Sex *</Label>
                                                    <select
                                                        className="form-control"
                                                        name="sex"
                                                        id="sex"
                                                        {...register("sex")}
                                                        style={{border: "1px solid #04C4D9"}}
                                                    >
                                                        <option value={""}></option>
                                                        {genderRows}
                                                    </select>
                                                    {errors.sex && <p>{errors.sex.message}</p>}
                                                </FormGroup>
                                            </div>

                                            <div className="form-group  col-md-4">
                                                <FormGroup>
                                                    <Label>Employment Status *</Label>
                                                    <select
                                                        className="form-control"
                                                        name="employmentStatus"
                                                        id="employmentStatus"
                                                        {...register("employmentStatus")}
                                                        style={{border: "1px solid #04C4D9"}}
                                                    >
                                                        <option value={""}></option>
                                                        {occupationRows}
                                                    </select>
                                                    {errors.lastName && <p>{errors.lastName.message}</p>}
                                                </FormGroup>
                                            </div>

                                            <div className="form-group  col-md-4">
                                                <FormGroup>
                                                    <Label>Highest Qualification *</Label>
                                                    <select
                                                        className="form-control"
                                                        name="highestQualification"
                                                        id="highestQualification"
                                                        {...register("highestQualification")}
                                                        style={{border: "1px solid #04C4D9"}}
                                                    >
                                                        <option value={""}></option>
                                                        {educationRows}
                                                    </select>
                                                    {errors.highestQualification && <p>{errors.highestQualification.message}</p>}
                                                </FormGroup>
                                            </div>
                                        </div>

                                        <div className={"row"}>
                                            <div className="form-group mb-3 col-md-3">
                                                <FormGroup>
                                                    <Label>Marital Status</Label>
                                                    <select
                                                        className="form-control"
                                                        name="maritalStatus"
                                                        id="maritalStatus"
                                                        {...register("maritalStatus")}
                                                        style={{border: "1px solid #04C4D9"}}
                                                    >
                                                        <option value={""}></option>
                                                        {maritalStatusRows}
                                                    </select>
                                                    {errors.maritalStatus && <p>{errors.maritalStatus.message}</p>}
                                                </FormGroup>
                                            </div>

                                            <div className="form-group mb-2 col-md-2">
                                                <FormGroup>
                                                    <Label>Date Of Birth</Label>
                                                    <div className="radio">
                                                        <label>
                                                            <input
                                                                type="radio"
                                                                value="Actual"
                                                                name="dateOfBirth"
                                                                defaultChecked
                                                                {...register("dateOfBirth")}
                                                                onChange={(e) => handleDateOfBirthChange(e)}
                                                                style={{border: "1px solid #04C4D9"}}
                                                            /> Actual
                                                        </label>
                                                    </div>
                                                    <div className="radio">
                                                        <label>
                                                            <input
                                                                type="radio"
                                                                value="Estimated"
                                                                name="dateOfBirth"
                                                                {...register("dateOfBirth")}
                                                                onChange={(e) => handleDateOfBirthChange(e)}
                                                                style={{border: "1px solid #04C4D9"}}
                                                            /> Estimated
                                                        </label>
                                                    </div>
                                                </FormGroup>
                                            </div>

                                            <div className="form-group mb-3 col-md-3">
                                                <FormGroup>
                                                    <Label>Date</Label>
                                                    <input
                                                        className="form-control"
                                                        type="date"
                                                        name="dob"
                                                        id="dob"
                                                        max={today}
                                                        {...register("dob")}
                                                        onChange={(e) => handleDobChange(e)}
                                                        style={{border: "1px solid #04C4D9"}}
                                                    />
                                                    {errors.dob && <p>{errors.dob.message}</p>}
                                                </FormGroup>
                                            </div>

                                            <div className="form-group mb-3 col-md-3">
                                                <FormGroup>
                                                    <Label>Age</Label>
                                                    <input
                                                        className="form-control"
                                                        type="number"
                                                        name="age"
                                                        id="age"
                                                        {...register("age")}
                                                        disabled={ageDisabled}
                                                        onChange={(e) => handleAgeChange(e)}
                                                        style={{border: "1px solid #04C4D9"}}
                                                    />
                                                </FormGroup>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div className="card">
                                <div className="card-header" style={{backgroundColor:"#04C4D9",color:'#fff',fontWeight:'bolder'}}>
                                    <h5 className="card-title">Contact Details</h5>
                                </div>

                                <div className="card-body">
                                    <div className={"row"}>
                                        <div className="form-group  col-md-4">
                                            <FormGroup>
                                                <Label>Phone Number *</Label>
                                                <input
                                                    className="form-control"
                                                    type="tel"
                                                    name="pnumber"
                                                    id="pnumber"
                                                    {...register("pnumber")}
                                                    placeholder="(234)7099999999"
                                                    style={{border: "1px solid #04C4D9"}}
                                                />
                                                {errors.pnumber && <p>{errors.pnumber.message}</p>}
                                            </FormGroup>
                                        </div>

                                        <div className="form-group col-md-4">
                                            <FormGroup>
                                                <Label>Alt. Phone Number</Label>
                                                <input
                                                    className="form-control"
                                                    type="tel"
                                                    name="altPhoneNumber"
                                                    id="altPhoneNumber"
                                                    {...register("altPhonenumber")}
                                                    placeholder="(234)7099999999"
                                                    style={{border: "1px solid #04C4D9"}}
                                                />
                                                {errors.altPhonenumber && <p>{errors.altPhonenumber.message}</p>}
                                            </FormGroup>
                                        </div>

                                        <div className="form-group col-md-4">
                                            <FormGroup>
                                                <Label>Email</Label>
                                                <input
                                                    className="form-control"
                                                    type="email"
                                                    name="email"
                                                    id="email"
                                                    {...register("email")}
                                                    style={{border: "1px solid #04C4D9"}}
                                                />
                                                {errors.email && <p>{errors.email.message}</p>}
                                            </FormGroup>
                                        </div>
                                    </div>

                                    <div className="row">
                                        <div className="form-group  col-md-4">
                                            <FormGroup>
                                                <Label>Country</Label>
                                                <select
                                                    className="form-control"
                                                    type="text"
                                                    name="country"
                                                    id="country"
                                                    style={{border: "1px solid #04C4D9"}}
                                                    {...register("countryId")}
                                                    onChange={(e) => onCountryChange(e)}>
                                                    <option value={""}></option>
                                                    {topLevelUnitCountryRows}
                                                </select>
                                                {errors.country && <p>{errors.country.message}</p>}
                                            </FormGroup>
                                        </div>

                                        <div className="form-group  col-md-4">
                                            <FormGroup>
                                                <Label>State</Label>
                                                <select
                                                    className="form-control"
                                                    type="text"
                                                    name="stateId"
                                                    id="stateId"
                                                    style={{border: "1px solid #04C4D9"}}
                                                    {...register("stateId")}
                                                    onChange={(e) => onStateChange(e)}>
                                                    <option value={""}></option>
                                                    {stateRows}
                                                </select>
                                            </FormGroup>
                                        </div>

                                        <div className="form-group  col-md-4">
                                            <FormGroup>
                                                <Label>Province/District/LGA</Label>
                                                <select
                                                    className="form-control"
                                                    type="text"
                                                    name="district"
                                                    id="district"
                                                    style={{border: "1px solid #04C4D9"}}
                                                    {...register("district")}>
                                                    <option value={""}></option>
                                                    {districtRows}
                                                </select>
                                            </FormGroup>
                                        </div>
                                    </div>

                                    <div className={"row"}>
                                        <div className="form-group  col-md-4">
                                            <FormGroup>
                                                <Label>Street Address</Label>
                                                <input
                                                    className="form-control"
                                                    type="text"
                                                    name="address"
                                                    id="address"
                                                    style={{border: "1px solid #04C4D9"}}
                                                    {...register("address")}
                                                />
                                                {errors.address && <p>{errors.address.message}</p>}
                                            </FormGroup>
                                        </div>

                                        <div className="form-group  col-md-4">
                                            <FormGroup>
                                                <Label>Landmark</Label>
                                                <input
                                                    className="form-control"
                                                    type="text"
                                                    name="landmark"
                                                    id="landmark"
                                                    style={{border: "1px solid #04C4D9"}}
                                                    {...register("landmark")}
                                                />
                                                {errors.landmark && <p>{errors.landmark.message}</p>}
                                            </FormGroup>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div className="card">
                                <div className="card-header" style={{backgroundColor:"#04C4D9",color:'#fff',fontWeight:'bolder'}}>
                                    <h5 className="card-title">Relationship / Next Of Kin</h5>
                                </div>
                                <div className="card-body">
                                    <div className="row">
                                        {
                                            contacts && contacts.length > 0 && (
                                                <div className="col-xl-12 col-lg-12">
                                                    <table style={{ width: '100%' }}>
                                                        <thead>
                                                        <tr>
                                                            <th>Relationship Type</th>
                                                            <th>Name</th>
                                                            <th>Phone</th>
                                                            <th>Address</th>
                                                            <th>Actions</th>
                                                        </tr>
                                                        </thead>
                                                        <tbody>
                                                        {contacts.map((item, index) => {
                                                            return (
                                                                <tr key={item.index}>
                                                                    <td>{ getRelationship(item.relationshipId) }</td>
                                                                    <td>{ getNames(item) }</td>
                                                                    <td>{ getPhoneContactPoint(item.contactPoint) }</td>
                                                                    <td>{ getAddress(item.address) }</td>
                                                                    <td>
                                                                        <button type="button"
                                                                                className="btn btn-default btn-light btn-sm editRow"
                                                                                onClick={(e) => handleEditRelative(item, index)}>
                                                                            <FontAwesomeIcon icon="edit" />
                                                                        </button>
                                                                        &nbsp;&nbsp;
                                                                        <button
                                                                            type="button"
                                                                            className="btn btn-danger btn-sm removeRow"
                                                                            onClick={(e) => handleDeleteRelative(index)}>
                                                                            <FontAwesomeIcon icon="trash" />
                                                                        </button>
                                                                    </td>
                                                                </tr>
                                                            );
                                                        })}
                                                        </tbody>
                                                    </table>
                                                </div>
                                            )
                                        }
                                        <div className="col-xl-12 col-lg-12">
                                            {
                                                showRelative && (
                                                    <div className="card">
                                                        <div className="card-body">
                                                            <div className="row">
                                                                <div className="form-group mb-3 col-md-3">
                                                                    <FormGroup>
                                                                        <Label for="relationshipType">Relationship Type *</Label>
                                                                        <select
                                                                            className="form-control"
                                                                            name="relationshipType"
                                                                            id="relationshipType"
                                                                            style={{border: "1px solid #04C4D9"}}
                                                                            {...register("relationshipType")}>
                                                                            <option value={""}></option>
                                                                            {relationshipRows}
                                                                        </select>
                                                                        {errors.relationshipType && <p>{errors.relationshipType.message}</p>}
                                                                    </FormGroup>
                                                                </div>

                                                                <div className="form-group mb-3 col-md-3">
                                                                    <FormGroup>
                                                                        <Label for="cfirstName">First Name *</Label>
                                                                        <input
                                                                            className="form-control"
                                                                            type="text"
                                                                            name="cfirstName"
                                                                            id="cfirstName"
                                                                            style={{border: "1px solid #04C4D9"}}
                                                                            {...register("cfirstName")}
                                                                        />
                                                                        {errors.cfirstName && <p>{errors.cfirstName.message}</p>}
                                                                    </FormGroup>
                                                                </div>

                                                                <div className="form-group mb-3 col-md-3">
                                                                    <FormGroup>
                                                                        <Label>Middle Name</Label>
                                                                        <input
                                                                            className="form-control"
                                                                            type="text"
                                                                            name="cmiddleName"
                                                                            id="cmiddleName"
                                                                            style={{border: "1px solid #04C4D9"}}
                                                                            {...register("cmiddleName")}
                                                                        />
                                                                        {errors.cmiddleName && <p>{errors.cmiddleName.message}</p>}
                                                                    </FormGroup>
                                                                </div>

                                                                <div className="form-group mb-3 col-md-3">
                                                                    <FormGroup>
                                                                        <Label>Last Name *</Label>
                                                                        <input
                                                                            className="form-control"
                                                                            type="text"
                                                                            name="clastName"
                                                                            id="clastName"
                                                                            style={{border: "1px solid #04C4D9"}}
                                                                            {...register("clastName")}
                                                                        />
                                                                        {errors.clastName && <p>{errors.clastName.message}</p>}
                                                                    </FormGroup>
                                                                </div>
                                                            </div>

                                                            <div className="row">
                                                                <div className="form-group mb-3 col-md-3">
                                                                    <FormGroup>
                                                                        <Label for="contactPhoneNumber">Phone Number</Label>
                                                                        <input
                                                                            className="form-control"
                                                                            type="text"
                                                                            name="contactPhoneNumber"
                                                                            id="contactPhoneNumber"
                                                                            style={{border: "1px solid #04C4D9"}}
                                                                            {...register("contactPhoneNumber")}
                                                                        />
                                                                        {errors.contactPhoneNumber && <p>{errors.contactPhoneNumber.message}</p>}
                                                                    </FormGroup>
                                                                </div>

                                                                <div className="form-group mb-3 col-md-3">
                                                                    <FormGroup>
                                                                        <Label for="contactEmail">Email</Label>
                                                                        <input
                                                                            className="form-control"
                                                                            type="text"
                                                                            name="contactEmail"
                                                                            id="contactEmail"
                                                                            style={{border: "1px solid #04C4D9"}}
                                                                            {...register("contactEmail")}
                                                                        />
                                                                        {errors.contactEmail && <p>{errors.contactEmail.message}</p>}
                                                                    </FormGroup>
                                                                </div>

                                                                <div className="form-group mb-3 col-md-3">
                                                                    <FormGroup>
                                                                        <Label for="contactAddress">Address</Label>
                                                                        <input
                                                                            className="form-control"
                                                                            type="text"
                                                                            name="contactAddress"
                                                                            id="contactAddress"
                                                                            style={{border: "1px solid #04C4D9"}}
                                                                            {...register("contactAddress")}
                                                                        />
                                                                        {errors.contactAddress && <p>{errors.contactAddress.message}</p>}
                                                                    </FormGroup>
                                                                </div>
                                                            </div>

                                                            <div className="row">
                                                                <div className="">
                                                                    <MatButton
                                                                        type="button"
                                                                        variant="contained"
                                                                        color="primary"
                                                                        className={classes.button}
                                                                        onClick={handleSaveRelationship}
                                                                    >
                                                                        Add
                                                                    </MatButton>
                                                                </div>

                                                                <div className="">
                                                                    <MatButton
                                                                        type="button"
                                                                        variant="contained"
                                                                        color="secondary"
                                                                        className={classes.button}
                                                                        onClick={handleCancelSaveRelationship}
                                                                    >
                                                                        Cancel
                                                                    </MatButton>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                )
                                            }
                                        </div>
                                    </div>

                                    <div className="row">
                                        <MatButton
                                            type="button"
                                            variant="contained"
                                            color="primary"
                                            className={classes.button}
                                            startIcon={<AddIcon />}
                                            onClick={handleAddRelative}
                                            style={{backgroundColor:'#04C4D9',fontWeight:"bolder"}}
                                        >
                                            Add a Relative/Next Of Kin
                                        </MatButton>
                                    </div>
                                </div>
                            </div>

                            {saving ? <Spinner /> : ""}

                            <br />

                            {userDetail ===null ? (
                                    <MatButton
                                        type="submit"
                                        variant="contained"
                                        color="primary"
                                        className={classes.button}
                                        startIcon={<SaveIcon />}
                                    >
                                        {!saving ? (
                                            <span style={{ textTransform: "capitalize" }}>Save</span>
                                        ) : (
                                            <span style={{ textTransform: "capitalize" }}>Saving...</span>
                                        )}
                                    </MatButton>
                                )
                                :
                                (
                                    <MatButton
                                        type="submit"
                                        variant="contained"
                                        color="primary"
                                        className={classes.button}
                                        startIcon={<SaveIcon />}
                                    >
                                        {!saving ? (
                                            <span style={{ textTransform: "capitalize" }}>Save</span>
                                        ) : (
                                            <span style={{ textTransform: "capitalize" }}>Saving...</span>
                                        )}
                                    </MatButton>
                                )
                            }
                            <MatButton
                                variant="contained"
                                className={classes.button}
                                startIcon={<CancelIcon />}
                                onClick={handleCancel}
                            >
                                <span style={{ textTransform: "capitalize" }}>Cancel</span>
                            </MatButton>
                        </Form>
                    </div>
                </CardContent>
            </Card>
        </>
    );
};

export default UserRegistration
