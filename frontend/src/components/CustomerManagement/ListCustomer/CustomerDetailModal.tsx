import React from 'react'
import { Modal, Button, Row, Col, Form } from 'react-bootstrap'
import { Link } from 'react-router-dom'
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
    faAddressBook, faAddressCard, faCalendarDays, faEnvelope, faFaceSmile, faUser, faLocationDot,
    faPhone
} from "@fortawesome/free-solid-svg-icons";
import { CustomerDetailModalProps } from '../../Interface/UtilsInterface'
const CustomerDetailModal: React.FC<CustomerDetailModalProps> = ({ show, handleClose, customer }) => {
    return (
        <Modal show={show} onHide={handleClose} centered size='xl'>
            <Modal.Header closeButton>
                <Modal.Title>Customer Details</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <Form>
                    <Row className="mb-4">
                        <Col md={4}>
                            <Form.Group className="text-start">
                                <Form.Label htmlFor="fullName" className="fw-bold">
                                    Full name <span style={{ color: "red" }}>(*):</span>
                                </Form.Label>
                                <div className="d-flex align-items-center">
                                    <FontAwesomeIcon icon={faAddressBook} className="me-3" />
                                    <Form.Control
                                        id="fullName"
                                        type="text"
                                        value={customer?.employeeName}
                                        disabled>
                                    </Form.Control>
                                </div>
                            </Form.Group>
                        </Col>
                        <Col md={4}>
                            <Form.Group className="text-start">
                                <Form.Label htmlFor="dateOfBirth" className="fw-bold">
                                    Date of birth<span style={{ color: "red" }}>(*):</span>
                                </Form.Label>
                                <div className="d-flex align-items-center">
                                    <FontAwesomeIcon icon={faCalendarDays} className="me-3" />
                                    <Form.Control
                                        id="dateOfBirth"
                                        type="date"
                                        value={customer?.dateOfBirth}
                                        disabled
                                    />
                                </div>
                            </Form.Group>
                        </Col>
                        <Col md={4}>
                            <Form.Group className="text-start">
                                <Form.Label htmlFor="gender" className="fw-bold">
                                    Gender
                                </Form.Label>
                                <div className="d-flex align-items-center">
                                    <FontAwesomeIcon icon={faFaceSmile} className="me-3" />
                                    <Form.Check
                                        id="gender"
                                        type="radio"
                                        label="Male"
                                        value="Male"
                                        defaultChecked={customer?.gender === "Male"}
                                        disabled
                                    />
                                    <Form.Check
                                        id="gender"
                                        type="radio"
                                        label="Female"
                                        value="Female"
                                        defaultChecked={customer?.gender === "Female"}
                                        disabled
                                    />
                                </div>
                            </Form.Group>
                        </Col>
                    </Row>
                    <Row className="mb-4">
                        <Col>
                            <Form.Group className="text-start">
                                <Form.Label htmlFor="identityCard" className="fw-bold">
                                    Identity card<span style={{ color: "red" }}>(*):</span>
                                </Form.Label>
                                <div className="d-flex align-items-center">
                                    <FontAwesomeIcon icon={faAddressCard} className="me-3" />
                                    <Form.Control
                                        id="identityCard"
                                        type="text"
                                        value={customer?.identityCard}
                                        disabled
                                    />
                                </div>
                            </Form.Group>
                        </Col>
                        <Col>
                            <Form.Group className="text-start">
                                <Form.Label htmlFor="username" className="fw-bold">
                                    Username<span style={{ color: "red" }}>(*):</span>
                                </Form.Label>
                                <div className="d-flex align-items-center">
                                    <FontAwesomeIcon icon={faUser} className="me-3" />
                                    <Form.Control
                                        id="username"
                                        type="text"
                                        value={customer?.username}
                                        disabled
                                    />
                                </div>
                            </Form.Group>
                        </Col>
                    </Row>
                    <Row className="mb-4">
                        <Col>
                            <Form.Group className="text-start">
                                <Form.Label htmlFor="email" className="fw-bold">
                                    Email<span style={{ color: "red" }}>(*):</span>
                                </Form.Label>
                                <div className="d-flex align-items-center">
                                    <FontAwesomeIcon icon={faEnvelope} className="me-3" />
                                    <Form.Control
                                        id="email"
                                        type="text"
                                        value={customer?.email}
                                        disabled
                                    />
                                </div>
                            </Form.Group>
                        </Col>
                        <Col>
                            <Form.Group className="text-start">
                                <Form.Label htmlFor="phone" className="fw-bold">
                                    Phone<span style={{ color: "red" }}>(*):</span>
                                </Form.Label>
                                <div className="d-flex align-items-center">
                                    <FontAwesomeIcon icon={faPhone} className="me-3" />
                                    <Form.Control
                                        id="phone"
                                        type="text"
                                        value={customer?.phone}
                                        disabled
                                    />
                                </div>
                            </Form.Group>
                        </Col>
                    </Row>
                    <Row className='mb-4'>
                        <Col>
                            <Form.Group className="text-start">
                                <Form.Label htmlFor="address" className="fw-bold">
                                    Address<span style={{ color: "red" }}>(*):</span>
                                </Form.Label>
                                <div className="d-flex align-items-center">
                                    <FontAwesomeIcon icon={faLocationDot} className="me-3" />
                                    <Form.Control
                                        id="address"
                                        type="text"
                                        as="textarea"
                                        value={customer?.address}
                                        disabled
                                    />
                                </div>
                            </Form.Group>
                        </Col>
                    </Row>
                </Form>
            </Modal.Body>
            <Modal.Footer>
                <Link to={`/customer/update/${customer?.id}`}>
                    <Button className='m-1 rounded-0 text-white' variant='warning'>
                        Update Customer
                    </Button>
                </Link>
                <Button className='m-1 rounded-0' variant="secondary" onClick={handleClose}>Close</Button>
            </Modal.Footer>
        </Modal>
    )
}

export default CustomerDetailModal