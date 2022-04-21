import React from 'react';
import PropTypes from 'prop-types';
import { withStyles } from '@material-ui/core/styles';
import classNames from 'classnames';
import {
    Accordion,
    AccordionSummary,
    AccordionDetails,
    AccordionActions
} from '@material-ui/core';
import Typography from '@material-ui/core/Typography';
import ExpandMoreIcon from '@material-ui/icons/ExpandMore';
//import Chip from '@material-ui/core/Chip';
import Divider from '@material-ui/core/Divider';
import { Button } from 'semantic-ui-react';
import { Grid, Step, Label, Segment, Icon } from "semantic-ui-react";
import 'semantic-ui-css/semantic.min.css';
import { Col, Row } from "reactstrap";

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

    const getHospitalNumber = (identifier) => {
        const identifiers = JSON.parse(identifier);
        const hospitalNumber = identifiers.identifier.find(obj => obj.type == 'HospitalNumber');
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
        const contactPoints = JSON.parse(contactPoint);
        const phoneContact = contactPoints.contactPoint.find(obj => obj.type == 'phone');
        return phoneContact ? phoneContact.value : '';
    };

    const getAddress = (address) => {
        const addresses = JSON.parse(address);
        const city = addresses && addresses.address && addresses.address.length > 0 ? addresses.address[0].city : null;
        return city;
    };

    return (
        <div className={classes.root}>
            <Accordion defaultExpanded>
                <AccordionSummary>

                    <Row>

                        <Col md={11}>
                            <Row className={"mt-1"}>
                                <Col md={12} className={classes.root2}>
                                    <b style={{fontSize: "25px"}}>
                                        { patientObj.surname + ", " + patientObj.firstname + " " + patientObj.otherName }
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
                        <b>{JSON.parse(patientObj.gender).value }</b>
                    </span>
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

                                <Col md={12}>
                                    <Label.Group >

                                        <Label color={"blue"} size={"mini"}>
                                            Transfer

                                        </Label>

                                    </Label.Group>

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
                    <div className={classNames(classes.column, classes.helper)}>
                        <Typography variant="caption">
                            <Label color={"red"} size={"mini"}>
                                Biometric Status
                                <Label.Detail>Not Captured</Label.Detail>
                            </Label>

                            <Label color={"green"} size={"mini"}>
                                Patient Status
                                <Label.Detail>Active</Label.Detail>
                            </Label>
                        </Typography>
                    </div>
                </AccordionDetails>
                <Divider />
                <AccordionActions>

                </AccordionActions>
            </Accordion>

        </div>
    );
}

PatientCard.propTypes = {
    classes: PropTypes.object.isRequired,
};

export default withStyles(styles)(PatientCard);