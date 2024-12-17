import React from 'react'
import { Modal, Button, Row, Col, Form } from 'react-bootstrap'
import { Link } from 'react-router-dom'
import { InjectionScheduleDetailModalProps } from '../../Interface/UtilsInterface'



const InjectionScheduleDetailModal: React.FC<InjectionScheduleDetailModalProps> = ({ show, handleClose, schedule }) => {
    return (
        <Modal show={show} onHide={handleClose} centered size='xl'>
            <Modal.Header closeButton>
                <Modal.Title>Injection Schedule Details</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <Form>
                    <Row className="mb-4">
                        <Col md={3}>
                            <Form.Group className="text-start">
                                <Form.Label htmlFor="vaccineId" className="fw-bold">
                                    Vaccine <span style={{ color: "red" }}>(*):</span>
                                </Form.Label>
                                <Form.Control
                                    id="vaccineId"
                                    type="text"
                                    value={schedule?.vaccineName}
                                    disabled>
                                </Form.Control>
                            </Form.Group>
                        </Col>
                        <Col md={3}>
                            <Form.Group className="text-start">
                                <Form.Label htmlFor="startDate" className="fw-bold">
                                    From<span style={{ color: "red" }}>(*):</span>
                                </Form.Label>
                                <Form.Control
                                    id="startDate"
                                    type="date"
                                    value={schedule?.startDate}
                                    disabled
                                />
                            </Form.Group>
                        </Col>
                        <Col md={3}>
                            <Form.Group className="text-start">
                                <Form.Label htmlFor="endDate" className="fw-bold">
                                    To<span style={{ color: "red" }}>(*):</span>
                                </Form.Label>
                                <Form.Control
                                    id="endDate"
                                    type="date"
                                    value={schedule?.endDate}
                                    disabled
                                />
                            </Form.Group>
                        </Col>
                        <Col md={3}>
                            <Form.Group className="text-start">
                                <Form.Label htmlFor="injectionTimes" className="fw-bold">
                                    Inject per day<span style={{ color: "red" }}>(*):</span>
                                </Form.Label>
                                <Form.Control
                                    id="injectionTimes"
                                    type="text"
                                    value={schedule?.injectionTimes}
                                    disabled
                                />
                            </Form.Group>
                        </Col>
                    </Row>
                    <Row className="mb-4">
                        <Col md={4}>
                            <Form.Group className="text-start">
                                <Form.Label htmlFor="place" className="fw-bold">
                                    Place<span style={{ color: "red" }}>(*):</span>
                                </Form.Label>
                                <Form.Control
                                    id="place"
                                    type="text"
                                    as="textarea"
                                    rows={4}
                                    value={schedule?.place}
                                    disabled
                                />
                            </Form.Group>
                        </Col>
                        <Col md={8}>
                            <Form.Group className="text-start">
                                <Form.Label htmlFor="description" className="fw-bold">
                                    Note:
                                </Form.Label>
                                <Form.Control
                                    id="description"
                                    type="text"
                                    as="textarea"
                                    rows={4}
                                    value={schedule?.description}
                                    disabled
                                />
                            </Form.Group>
                        </Col>
                    </Row>

                </Form>
            </Modal.Body>
            <Modal.Footer>
                <Link to={`/schedule/update/${schedule?.id}`}>
                    <Button className='m-1 rounded-0 text-white' variant='warning'>
                        Update Injection Schedule
                    </Button>
                </Link>
                <Button className='m-1 rounded-0' variant="secondary" onClick={handleClose}>Close</Button>
            </Modal.Footer>
        </Modal>
    )
}

export default InjectionScheduleDetailModal