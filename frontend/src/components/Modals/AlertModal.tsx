import React from 'react'
import { Modal, Button, Row } from 'react-bootstrap';
interface AlertModalProps {
    show: boolean;
    handleClose: () => void;
    message: string;
}

const AlertModal: React.FC<AlertModalProps> = ({ show, handleClose, message }) => {
    return (
        <Modal show={show} onHide={handleClose} keyboard={false} centered>
            <Modal.Header closeButton>
                <Modal.Title>Confirm</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <div
                    style={{ fontWeight: "inherit", paddingBottom: '10px' }}
                    className="text-center"
                >
                    {message}
                </div>
                <Row className="d-flex justify-content-evenly">
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

export default AlertModal