import React, { useState, useRef, useEffect } from "react";
import MaterialTable from "material-table";
import Button from "@material-ui/core/Button";
import axios from "axios";
import { url as baseUrl, token } from "../../../api";
import { FaEye } from "react-icons/fa";
import SplitActionButton from "./SplitActionButton";
import PatientRecapture from "./PatientRecapture";
import Recapture from "./Recapture";
import MatButton from "@material-ui/core/Button";
import FingerprintIcon from "@mui/icons-material/Fingerprint";

import { forwardRef } from "react";
//import { Button} from "react-bootstrap";
import AddBox from "@material-ui/icons/AddBox";
import ArrowUpward from "@material-ui/icons/ArrowUpward";
import Check from "@material-ui/icons/Check";
import ChevronLeft from "@material-ui/icons/ChevronLeft";
import ChevronRight from "@material-ui/icons/ChevronRight";
import Clear from "@material-ui/icons/Clear";
import DeleteOutline from "@material-ui/icons/DeleteOutline";
import Edit from "@material-ui/icons/Edit";
import FilterList from "@material-ui/icons/FilterList";
import FirstPage from "@material-ui/icons/FirstPage";
import LastPage from "@material-ui/icons/LastPage";
import Remove from "@material-ui/icons/Remove";
import SaveAlt from "@material-ui/icons/SaveAlt";
import Search from "@material-ui/icons/Search";
import ViewColumn from "@material-ui/icons/ViewColumn";
import FormGroup from "@mui/material/FormGroup";
import FormControlLabel from "@mui/material/FormControlLabel";
import Checkbox from "@mui/material/Checkbox";
import TablePagination from "@mui/material/TablePagination";

const tableIcons = {
  Add: forwardRef((props, ref) => <AddBox {...props} ref={ref} />),
  Check: forwardRef((props, ref) => <Check {...props} ref={ref} />),
  Clear: forwardRef((props, ref) => <Clear {...props} ref={ref} />),
  Delete: forwardRef((props, ref) => <DeleteOutline {...props} ref={ref} />),
  DetailPanel: forwardRef((props, ref) => (
    <ChevronRight {...props} ref={ref} />
  )),
  Edit: forwardRef((props, ref) => <Edit {...props} ref={ref} />),
  Export: forwardRef((props, ref) => <SaveAlt {...props} ref={ref} />),
  Filter: forwardRef((props, ref) => <FilterList {...props} ref={ref} />),
  FirstPage: forwardRef((props, ref) => <FirstPage {...props} ref={ref} />),
  LastPage: forwardRef((props, ref) => <LastPage {...props} ref={ref} />),
  NextPage: forwardRef((props, ref) => <ChevronRight {...props} ref={ref} />),
  PreviousPage: forwardRef((props, ref) => (
    <ChevronLeft {...props} ref={ref} />
  )),
  ResetSearch: forwardRef((props, ref) => <Clear {...props} ref={ref} />),
  Search: forwardRef((props, ref) => <Search {...props} ref={ref} />),
  SortArrow: forwardRef((props, ref) => <ArrowUpward {...props} ref={ref} />),
  ThirdStateCheck: forwardRef((props, ref) => <Remove {...props} ref={ref} />),
  ViewColumn: forwardRef((props, ref) => <ViewColumn {...props} ref={ref} />),
};

const PreviousRecapture = (props) => {
  let createdDate = props.patientObj.createdDate.split("T")[0];
  let currentDate = new Date().toISOString().split("T")[0];

  const [recapturedFingered, setRecapturedFingered] = useState([]);
  const [modal, setModal] = useState(false);
  const [modalNew, setModalNew] = useState(false);
  const toggle = () => setModal(!modal);
  const toggleNew = () => setModalNew(!modalNew);

  const tableRef = useRef(null);
  const [loading, setLoading] = useState("");
  const [recaptures, setRecaptures] = useState([]);
  const [biometrics, setBiometrics] = useState([]);
  const [currentPage, setCurrentPage] = useState(1);

  const getRecaptureCount = () => {
    axios
      .get(`${baseUrl}biometrics/grouped/person/${props.patientId}`, {
        headers: { Authorization: `Bearer ${token}` },
      })
      .then((response) => {
        //console.log(response.data);
        setRecapturedFingered(response.data);
      });
  };

  useEffect(() => {
    getRecaptureCount();
  }, []);

  const handleChangePage = (page) => {
    setCurrentPage(page + 1);
  };

  function actionItems(row) {
    //console.log(row);
    axios
      .get(
        `${baseUrl}biometrics?personUuid=${row.personUuid}&recapture=${row.recapture}`,
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      )
      .then((response) => {
        toggle();
        let biometricData = response.data.filter(
          (data) => data.date === row.captureDate
        );
        //console.log("gotten", biometricData);
        setBiometrics(biometricData);
      });
    //.error((err) => console.log(err));
  }

  return (
    <>
      <h4>
        {" "}
        Patient captured count : <b>{recapturedFingered.length}</b>
      </h4>
      {createdDate !== currentDate ? (
        <MatButton
          className=" float-right mr-1"
          variant="contained"
          floated="left"
          startIcon={<FingerprintIcon />}
          style={{
            backgroundColor: "#014d88",
            color: "#fff",
            height: "35px",
            float: "right",
            //marginBottom: "40px",
          }}
          onClick={toggleNew}
        >
          <span style={{ textTransform: "capitalize" }}>Recapture</span>
        </MatButton>
      ) : (
        ""
      )}
      <br />
      <br />
      <br />
      <MaterialTable
        tableRef={tableRef}
        /*onSearchChange={(e) => {
          handleSearchChange(e);
      }}*/
        icons={tableIcons}
        title={`Previous Recaptured Biometrics`}
        columns={[
          {
            title: "Captured Date",
            field: "captureDate",
            filtering: false,
            // hidden: enablePPI,
          },
          {
            title: "Number of Fingers Recaptured",
            field: "count",
            filtering: false,
          },
          //{ title: "data", field: "data", hidden: true },
          { title: "Actions", field: "actions", filtering: false },
        ]}
        isLoading={loading}
        data={
          recapturedFingered &&
          recapturedFingered
            .filter((record) => {
              return record.archived === 0;
            })
            .map((row) => ({
              captureDate: row.captureDate,
              count: row.count === null ? 0 : row.count,
              //data: actionItems(row),
              actions: (
                <Button
                  style={{ backgroundColor: "#014d88", color: "#fff" }}
                  onClick={() => actionItems(row)}
                >
                  View
                </Button>
              ),
            }))
          // (query) =>
          // new Promise((resolve, reject) =>
          //   axios
          //     .get(`${baseUrl}biometrics/grouped/person/${props.patientId}`, {
          //       headers: { Authorization: `Bearer ${token}` },
          //     })
          //     .then((response) => response)
          //     .then((result) => {
          //       resolve({
          //         data: result.data
          //           .filter((record) => {
          //             return record.archived === 0;
          //           })
          //           .map((row) => ({
          //             captureDate: row.captureDate,
          //             count: row.count === null ? 0 : row.count,
          //             //data: actionItems(row),
          //             actions: (
          //               <Button
          //                 style={{ backgroundColor: "#014d88", color: "#fff" }}
          //                 onClick={() => actionItems(row)}
          //               >
          //                 View
          //               </Button>
          //             ),
          //           })),
          //         page: query.page,
          //         totalCount: result.data.length,
          //       });
          //     })
          // )
        }
        options={{
          headerStyle: {
            backgroundColor: "#014d88",
            color: "#fff",
            fontSize: "16px",
            padding: "10px",
            fontWeight: "bolder",
          },
          searchFieldStyle: {
            width: "50%",
          },
          filtering: false,
          exportButton: false,
          searchFieldAlignment: "left",
          pageSizeOptions: [10, 20, 100],
          pageSize: 10,
          debounceInterval: 400,
        }}
        onChangePage={handleChangePage}
        //localization={localization}
      />
      <PatientRecapture
        storedBiometrics={biometrics}
        modal={modal}
        toggle={toggle}
      />
      <Recapture
        toggle={toggleNew}
        modal={modalNew}
        patientId={props.patientId}
        age={props.age}
        getRecaptureCount={getRecaptureCount}
      />
    </>
  );
};

export default PreviousRecapture;
