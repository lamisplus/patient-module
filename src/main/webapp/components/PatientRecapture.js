import React from "react";
import { Button, Modal, ModalHeader, ModalBody, ModalFooter } from "reactstrap";
import { Dropdown, Badge } from "react-bootstrap";
import { Alert, AlertTitle } from "@material-ui/lab";
import moment from "moment";
import { Link, useHistory } from "react-router-dom";
import fingerprintimage from "../images/fingerprintimage.png";

const PatientRecapture = (props) => {
  //console.log(props.storedBiometrics);
  return (
    <>
      <Modal
        isOpen={props.modal}
        toggle={props.toggle}
        style={{ display: "flex", maxWidth: "85%", maxHeight: "80%" }}
        fullscreen="true"
      >
        <ModalHeader toggle={props.toggle}>
          {props.storedBiometrics[0]?.recapture === 0
            ? "Baseline Fingerprints"
            : "Previous Recaptured Fingerprints"}
        </ModalHeader>
        <ModalBody>
          <div>
            <div
              className=""
              style={{
                padding: "5px",
                flex: "10",
                boxShadow: "rgba(99, 99, 99, 0.2) 0px 2px 8px 0px",
                minHeight: "400px",
              }}
            >
              {props.storedBiometrics.length > 0 ? (
                <div
                  style={{ display: "flex", width: "100%", flexWrap: "wrap" }}
                >
                  {props.storedBiometrics.map((biometric, index) => (
                    <div
                      key={index}
                      style={{
                        minHeight: "120px",
                        padding: "5px",
                        width: "20%",
                      }}
                    >
                      <div className="card " style={{ borderRadius: "6px" }}>
                        <div
                          className="card-header align-items-start"
                          style={{ backgroundColor: "#fff" }}
                        >
                          <div>
                            <h6 className="fs-18 font-w500 mb-3 user-name">
                              <Link
                                to={"#"}
                                style={{
                                  color: "#014d88",
                                  fontSize: "14px",
                                  fontFamily: `"poppins",sans-serif`,
                                }}
                              >
                                {biometric.templateType}
                              </Link>
                            </h6>
                            <div
                              className="fs-9 text-nowrap"
                              style={{
                                fontSize: "10px",
                                color: "#992E62",
                                fontWeight: "bold",
                                fontFamily: `"poppins",sans-serif`,
                              }}
                            >
                              <i
                                className="fa fa-calendar-o me-3"
                                aria-hidden="true"
                              ></i>
                              {moment(biometric.lastModifiedDate).format(
                                "YYYY-MM-DD HH:mm"
                              )}
                            </div>
                          </div>

                          {/*Action button -- Dropdown menu*/}
                          {/* <Dropdown className="dropdown ms-auto">
                            <Dropdown.Toggle
                              as="button"
                              variant=""
                              drop="up"
                              className="btn sharp btn-primary "
                              id="tp-btn"
                              style={{
                                backgroundColor: "#014d88",
                                borderColor: "#014d88",
                                borderRadius: "5px",
                                marginRight: "-18px",
                                marginTop: "-10px",
                              }}
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
                                  <circle
                                    fill="#ffffff"
                                    cx="12"
                                    cy="12"
                                    r="2"
                                  />
                                  <circle
                                    fill="#ffffff"
                                    cx="12"
                                    cy="19"
                                    r="2"
                                  />
                                </g>
                              </svg>
                            </Dropdown.Toggle>
                            <Dropdown.Menu
                              alignRight={true}
                              className="dropdown-menu-right"
                            >
                              <Dropdown.Item
                                style={{ color: "red" }}
                                onClick={() =>
                                  deleteBiometric(
                                    biometric.id,
                                    biometric.templateType
                                  )
                                }
                              >
                                <DeleteIcon /> Delete
                              </Dropdown.Item>
                            </Dropdown.Menu>
                          </Dropdown> */}
                        </div>
                        <div className="card-body p-0 pb-2">
                          <ul className="list-group list-group-flush">
                            <li
                              className="list-group-item"
                              style={{
                                height: "100px",
                                display: "flex",
                                justifyContent: "center",
                              }}
                            >
                              <img
                                src={fingerprintimage}
                                alt=""
                                style={{ height: "80px" }}
                              />
                            </li>

                            <li className="list-group-item">
                              <Badge
                                variant="info badge-xs light"
                                className="card-link float-end"
                              >
                                {biometric.imageQuality !== null
                                  ? biometric.imageQuality + "%"
                                  : "N/A"}{" "}
                                {/*{contact.version}*/}
                              </Badge>
                              <span className="mb-0 title">
                                Fingerprint Quality {biometric.iso}
                              </span>{" "}
                              :
                              {/* <span className="text-black desc-text ms-2">
                                <Badge
                                  variant={
                                    biometric.iso === true
                                      ? "primary badge-xs"
                                      : "danger badge-xs"
                                  }
                                >
                                  <i
                                    className="fa fa-check-square me-2 scale4"
                                    aria-hidden="true"
                                  ></i>{" "}
                                </Badge>
                              </span> */}
                            </li>
                          </ul>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              ) : (
                <>
                  <div className="flex-grow-8">
                    <div className="flex-grow-8">
                      <Alert severity="info">
                        <AlertTitle style={{ height: "400px" }}>
                          <strong>No biometrics captured</strong>
                        </AlertTitle>
                      </Alert>
                    </div>
                  </div>
                </>
              )}
            </div>
          </div>
        </ModalBody>
        <ModalFooter>
          {/* <Button color="primary" onClick={props.toggle}>
            Do Something
          </Button>{" "}
          <Button color="secondary" onClick={props.toggle}>
            Cancel
          </Button> */}
        </ModalFooter>
      </Modal>
    </>
  );
};

export default PatientRecapture;
