import React from 'react'
import { Modal, Button, Row } from 'react-bootstrap'
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faCircleInfo } from "@fortawesome/free-solid-svg-icons";

interface ConfirmModalProps {
    show: boolean;
    handleClose: () => void;
    message: string;
    handleLogic: () => void;
}

const ConfirmModal: React.FC<ConfirmModalProps> = ({ show, handleClose, message, handleLogic }) => {
    return (
        <Modal show={show} onHide={handleClose} keyboard={false} centered>

            <Modal.Header closeButton>
                <Modal.Title style={{ fontSize: '18px' }}>Confirm</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <div
                    style={{ fontWeight: "inherit", paddingBottom: '10px' }}
                    className="text-center"
                >
                    <FontAwesomeIcon icon={faCircleInfo} style={{ color: "#069369" }} /> {message}
                </div>
                <Row className="d-flex justify-content-evenly">
                    <Button variant="outline-secondary" onClick={handleLogic} style={{ width: '100px' }}>
                        OK
                    </Button>
                    <Button variant="outline-secondary" onClick={handleClose} style={{ width: '100px' }}>
                        Close
                    </Button>
                </Row>
            </Modal.Body>
            <Modal.Footer>
            </Modal.Footer>
        </Modal>
    )
}

export default ConfirmModal