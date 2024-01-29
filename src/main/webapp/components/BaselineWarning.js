import React from "react";
import { Button, Modal, ModalHeader, ModalBody, ModalFooter } from "reactstrap";

const BaselineWarning = (props) => {
  return (
    <>
      <Modal isOpen={props.modal} toggle={props.toggle}>
        <ModalHeader toggle={props.toggle}></ModalHeader>
        <ModalBody>
          <div>
            <p>
              <b>
                Are you sure, You want to replace this baseline fingerprints?
              </b>
            </p>
          </div>
        </ModalBody>
        <ModalFooter>
          <Button
            color="success"
            onClick={() => props.submitReplacedBaselinePrints()}
          >
            Yes
          </Button>{" "}
          <Button color="danger" onClick={props.toggle}>
            Cancel
          </Button>
        </ModalFooter>
      </Modal>
    </>
  );
};

export default BaselineWarning;
