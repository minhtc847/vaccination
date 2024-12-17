import React from 'react'
import { Modal, Button, Row, Col, Form } from 'react-bootstrap'
import { Link } from 'react-router-dom'
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
    faAddressBook, faAddressCard, faCalendarDays, faCircleDot, faEnvelope, faFaceSmile, faImage, faLandmark, faLocationDot
} from "@fortawesome/free-solid-svg-icons";
import { EmployeeDetailModalProps } from '../../Interface/UtilsInterface'
const EmployeeDetailModal: React.FC<EmployeeDetailModalProps> = ({ show, handleClose, employee }) => {
    return (
        <Modal show={show} onHide={handleClose} centered size='xl'>
            <Modal.Header closeButton>
                <Modal.Title>Employee Details</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <Form>
                    <Row className="mb-4">
                        <Col>
                            <Form.Group className="text-start">
                                <Form.Label htmlFor="employeeId" className="fw-bold">
                                    Employee ID:
                                </Form.Label>
                                <div className="d-flex align-items-center">
                                    <FontAwesomeIcon icon={faAddressBook} className="me-3" />
                                    <Form.Control
                                        id="employeeId"
                                        type="text"
                                        value={employee?.employeeId}
                                        disabled
                                    />
                                </div>
                            </Form.Group>
                        </Col>
                        <Col>
                            <Form.Group className="text-start">
                                <Form.Label htmlFor="employeeName" className="fw-bold">
                                    Employee Name:
                                </Form.Label>
                                <div className="d-flex align-items-center">
                                    <FontAwesomeIcon icon={faAddressBook} className="me-3" />
                                    <Form.Control
                                        id="employeeName"
                                        type="text"
                                        value={employee?.employeeName}
                                        disabled
                                    />
                                </div>
                            </Form.Group>
                        </Col>
                        <Col>
                            <Form.Group className="text-start">
                                <Form.Label className="fw-bold">Gender:</Form.Label>
                                <div className="d-flex align-items-center">
                                    <FontAwesomeIcon icon={faFaceSmile} className="me-3" />
                                    <Form.Control
                                        id="gender"
                                        type="text"
                                        value={employee?.gender}
                                        disabled
                                    />
                                </div>
                            </Form.Group>
                        </Col>
                    </Row>
                    <Row className="mb-4">
                        <Col>
                            <Form.Group className="text-start">
                                <Form.Label htmlFor="dateOfBirth" className="fw-bold">
                                    Date of birth:
                                </Form.Label>
                                <div className="d-flex align-items-center">
                                    <FontAwesomeIcon icon={faCalendarDays} className="me-3" />
                                    <Form.Control
                                        id="dateOfBirth"
                                        type="text"
                                        value={employee?.dateOfBirth}
                                        disabled
                                    />
                                </div>
                            </Form.Group>
                        </Col>
                        <Col>
                            <Form.Group className="text-start">
                                <Form.Label htmlFor="phone" className="fw-bold">
                                    Phone:
                                </Form.Label>
                                <div className="d-flex align-items-center">
                                    <FontAwesomeIcon icon={faAddressCard} className="me-3" />
                                    <Form.Control
                                        id="phone"
                                        type="text"
                                        value={employee?.phone}
                                        disabled
                                    />
                                </div>
                            </Form.Group>
                        </Col>
                        <Col>
                            <Form.Group className="text-start">
                                <Form.Label htmlFor="email" className="fw-bold">
                                    Email:
                                </Form.Label>
                                <div className="d-flex align-items-center">
                                    <FontAwesomeIcon icon={faEnvelope} className="me-3" />
                                    <Form.Control
                                        id="email"
                                        type="text"
                                        value={employee?.email}
                                        disabled
                                    />
                                </div>
                            </Form.Group>
                        </Col>
                    </Row>
                    <Row className="mb-4">
                        <Col>
                            <Form.Group className="text-start">
                                <Form.Label htmlFor="position" className="fw-bold">
                                    Position:
                                </Form.Label>
                                <div className="d-flex align-items-center">
                                    <FontAwesomeIcon icon={faCircleDot} className="me-3" />
                                    <Form.Control
                                        id="position"
                                        type="text"
                                        value={employee?.position}
                                        disabled
                                    />
                                </div>
                            </Form.Group>
                        </Col>
                        <Col>
                            <Form.Group className="text-start">
                                <Form.Label htmlFor="workingPlace" className="fw-bold">
                                    Working Place:
                                </Form.Label>
                                <div className="d-flex align-items-center">
                                    <FontAwesomeIcon icon={faLandmark} className="me-3" />
                                    <Form.Control
                                        id="workingPlace"
                                        type="text"
                                        value={employee?.workingPlace}
                                        disabled
                                    />
                                </div>
                            </Form.Group>
                        </Col>
                    </Row>
                    <Row className="mb-4">
                        <Col>
                            <Form.Group className="text-start">
                                <Form.Label htmlFor="address" className="fw-bold">
                                    Address:
                                </Form.Label>
                                <div className="d-flex align-items-center">
                                    <FontAwesomeIcon icon={faLocationDot} className="me-3" />
                                    <Form.Control
                                        id="address"
                                        type="text"
                                        as="textarea"
                                        value={employee?.address}
                                        disabled
                                    />
                                </div>
                            </Form.Group>
                        </Col>
                    </Row>
                    <Row className="mb-4">
                        <Col>
                            <Form.Group className="text-start">
                                <Form.Label htmlFor="image" className="fw-bold">
                                    Image:
                                </Form.Label>
                                <div className="d-flex align-items-center">
                                    <FontAwesomeIcon icon={faImage} className="me-3" />
                                    <img src={employee?.image ? employee?.image : "/assets/manager.png"} alt="Employee" style={{ maxWidth: '100px', maxHeight: '100px' }} />
                                </div>
                            </Form.Group>
                        </Col>
                    </Row>
                </Form>
            </Modal.Body>
            <Modal.Footer>

                <Link to={`/employee/update/${employee?.employeeId}`}>
                    <Button className='m-1 rounded-0 text-white' variant='warning'>
                        Update Employee
                    </Button>
                </Link>
                <Button className='m-1 rounded-0' variant="secondary" onClick={handleClose}>Close</Button>
            </Modal.Footer>
        </Modal>
    )
}

export default EmployeeDetailModal