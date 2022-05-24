import React, {useCallback, useEffect, useState} from 'react';
import { Link } from 'react-router-dom';
import {Button, Card, CardContent, FormControl, Grid, MenuItem, Paper, TextField, Typography, Box} from "@mui/material";
import { DataGrid } from '@mui/x-data-grid';
import AddIcon from '@mui/icons-material/Add';

const columns = [
    {
        field: 'weight',
        headerName: 'Weight',
        width: 200,
        editable: false,
    },
    {
        field: 'height',
        headerName: 'Height',
        width: 200,
        editable: false,
    },
    {
        field: 'temperature',
        headerName: 'Temperature',
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

const PatientVitals = () => {
    const rows = [];

    return (
        <Card>
            <CardContent>
                <Grid container spacing={2}>
                    <Grid item xs={3}>&nbsp;</Grid>
                    <Grid item xs={3}>&nbsp;</Grid>
                    <Grid item xs={3}>&nbsp;</Grid>
                    <Grid item xs={3}>
                        <Box
                            m={1}
                            display="flex"
                            justifyContent="center"
                            alignItems="center"
                        >
                            <Link to={{
                                pathname: "/add-patient-vital"
                            }}>
                                <Button
                                    variant="contained"
                                    style={{ backgroundColor: "black" }}
                                >
                                    <AddIcon /> Add Vital Signs
                                </Button>
                            </Link>
                        </Box>
                    </Grid>
                </Grid>
                <Grid container spacing={2}>
                    <Grid item xs={12}>
                        <Box height={"200px"}>
                            <DataGrid
                                rows={rows}
                                columns={columns}
                                pageSize={5}
                                rowsPerPageOptions={[5]}
                                disableSelectionOnClick
                            />
                        </Box>
                    </Grid>
                </Grid>
            </CardContent>
        </Card>
    );
};

export default PatientVitals;
