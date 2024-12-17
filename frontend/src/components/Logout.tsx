import React, { useState } from 'react';
import axios from 'axios';
import { Button, Modal, Row } from 'react-bootstrap';
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faCircleInfo } from "@fortawesome/free-solid-svg-icons";
import { useNavigate } from 'react-router-dom';
import BASE_URL from './Api/BaseApi';

function Logout() {
    const [show, setShow] = useState<boolean>(false);
    const token = localStorage.getItem('token');
    const nav = useNavigate();

    const handleClose = () => setShow(false);
    const handleShow = () => setShow(true);

    const logOut = () => {
        axios.get(`${BASE_URL}/vaccination/auth/logout`, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        })
        localStorage.removeItem('userInfo');
        localStorage.removeItem('token');
        nav("/");
    }

    return (
        <>
            <Modal show={show} onHide={handleClose} keyboard={false} centered>

                <Modal.Header closeButton>
                    <Modal.Title style={{ fontSize: '18px' }}>Confirm</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <div
                        style={{ fontWeight: "inherit", paddingBottom: '10px' }}
                        className="text-center"
                    >
                        <FontAwesomeIcon icon={faCircleInfo} style={{ color: "#069369" }} /> Are you sure to log out?
                    </div>
                    <Row className="d-flex justify-content-evenly">
                        <Button variant="outline-secondary" onClick={logOut} style={{ width: '100px' }}>
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
            <Button variant='link' style={{ textDecoration: 'none', color: 'white' }} onClick={handleShow}>Logout</Button>
        </>

    )
}

export default Logout