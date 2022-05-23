import React, {useCallback, useEffect, useState} from "react";
import {Controller, useForm} from "react-hook-form";
import {Button, Card, CardContent, FormControl, Grid, MenuItem, Paper, TextField, Typography} from "@mui/material";
import {LocalizationProvider} from "@mui/x-date-pickers/LocalizationProvider";
import {AdapterDateFns} from "@mui/x-date-pickers/AdapterDateFns";
import {DatePicker, TimePicker} from "@mui/x-date-pickers";
import axios from "axios";
import {token, url as baseUrl} from "../../../api";
import Swal from "sweetalert2";
import { format } from 'date-fns';

const CheckInPatient = () => {
    const { handleSubmit, control } = useForm();
    const [visitTypes, setVisitTypes] = useState([]);
    const onSubmit = async (data) => {
        try {
            await axios.post(`${baseUrl}patient/`, {
                "personId": 1,
                "visitStartDate": data.DateOfVisit
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
    const onError = () => {

    };

    const loadVisitTypes = useCallback(async () => {
        try {
            const response = await axios.get(`${baseUrl}application-codesets/v2/BOOKING STATUS`, { headers: {"Authorization" : `Bearer ${token}`} });
            setVisitTypes(response.data);
        } catch (e) {
            await Swal.fire({
                icon: 'error',
                title: 'Oops...',
                text: 'An error occurred while fetching BOOKING STATUS!',
            });
        }
    }, []);

    useEffect(() => {
        loadVisitTypes();
    }, [loadVisitTypes]);

    let visitTypesRows = null;
    if (visitTypes && visitTypes.length > 0) {
        visitTypesRows = visitTypes.map((visitType, index) => (
            <MenuItem key={visitType.id} value={visitType.id}>{visitType.display}</MenuItem>
        ));
    }

    return (
        <Card>
            <CardContent>
                <form onSubmit={handleSubmit(onSubmit, onError)}>
                    <Paper
                        style={{
                            display: "grid",
                            gridRowGap: "20px",
                            padding: "20px",
                            margin: "10px 10px",
                        }}>
                        <Typography variant="h6">Patient Appointment</Typography>
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
                            <Grid item xs={4}>
                                <FormControl fullWidth>
                                    <LocalizationProvider dateAdapter={AdapterDateFns}>
                                        <Controller
                                            name="DateOfVisit"
                                            control={control}
                                            defaultValue={null}
                                            render={({ field: { onChange, value }, fieldState: { error}}) => (
                                                <DatePicker
                                                    renderInput={(props) => <TextField {...props} />}
                                                    label="Date Of Visit"
                                                    selected={value}
                                                    value={value}
                                                    onChange={onChange}
                                                    error={!!error}
                                                    helperText={error ? error.message : null}
                                                />
                                            )}
                                            rules={{ required: 'Date Of Visit is Required' }}
                                        />
                                    </LocalizationProvider>
                                </FormControl>
                            </Grid>
                            <Grid item xs={4}>
                                <FormControl fullWidth>
                                    <LocalizationProvider dateAdapter={AdapterDateFns}>
                                        <Controller
                                            name="TimeOfVisit"
                                            control={control}
                                            defaultValue={null}
                                            render={({ field: { onChange, value }, fieldState: { error}}) => (
                                                <TimePicker
                                                    renderInput={(props) => <TextField {...props} />}
                                                    label="Time Of Visit"
                                                    selected={value}
                                                    value={value}
                                                    onChange={onChange}
                                                    error={!!error}
                                                    helperText={error ? error.message : null}
                                                />
                                            )}
                                            rules={{ required: 'Time Of Visit is Required' }}
                                        />
                                    </LocalizationProvider>
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
            </CardContent>
        </Card>
    );
};

export default CheckInPatient;
