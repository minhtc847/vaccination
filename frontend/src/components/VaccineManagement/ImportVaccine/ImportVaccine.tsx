import React, { ChangeEvent, useRef, useState } from 'react'
import { Button, Col, Container, Form, Row } from 'react-bootstrap'
import { Link } from 'react-router-dom';
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faImage } from "@fortawesome/free-solid-svg-icons";
import axios from 'axios';
import { Toast_Custom } from "../../Utils/Toast_Custom";
import LoadingOverlay from '../../Utils/LoadingOverlay/LoadingOverlay';
import BASE_URL from '../../Api/BaseApi';

const ImportVaccine: React.FC = () => {
    const fileInputRef = useRef<HTMLInputElement>(null);
    const [selectedFile, setSelectedFile] = useState<File | null>(null);
    const [isLoading, setIsLoading] = useState<boolean>(false);
    document.title = "Import Vaccine";

    const handleFileChange = (event: ChangeEvent<HTMLInputElement>) => {
        if (event.target.files && event.target.files[0]) {
            setSelectedFile(event.target.files[0]);
        }
    };

    const handleSubmit = async (event: React.FormEvent) => {
        event.preventDefault();
        const token = localStorage.getItem("token");
        setIsLoading(true);
        if (selectedFile) {
            const formData = new FormData();
            formData.append('file', selectedFile);

            try {
                const response = await axios.post(`${BASE_URL}/vaccination/vaccine/upload`, formData, {
                    headers: {
                        Authorization: `Bearer ${token}`,
                        "Content-Type": "multipart/form-data",
                    },
                });
                if (response.status === 200) {
                    Toast_Custom({
                        type: "success",
                        message: response.data,
                    })
                }
                setSelectedFile(null);
                if (fileInputRef.current) {
                    fileInputRef.current.value = '';
                }


            } catch (error) {
                if (axios.isAxiosError(error) && error.response) {
                    if (error.response.data.message) {
                        Toast_Custom({
                            type: "error",
                            message: error.response.data.message
                        })
                    }
                }
            }
        } else {
            Toast_Custom({
                type: "error",
                message: "No file selected"
            })
        }
        setTimeout(() => {
            setIsLoading(false);
        }, 500)
    };


    return (
        <Container className='pt-4'>
            {isLoading ? <LoadingOverlay /> : null}
            <h5 className='pb-3' style={{ fontWeight: "bold" }}>IMPORT VACCINE</h5>
            <Container style={{ backgroundColor: "white" }}>
                <Form onSubmit={handleSubmit}>
                    <Row className='p-2'>
                        <Form.Group className="text-start">
                            <Form.Label htmlFor="file" className="fw-bold">
                                Import file
                            </Form.Label>
                            <div className="d-flex align-items-center">
                                <FontAwesomeIcon icon={faImage} className="me-3" />
                                <Form.Control
                                    id="file"
                                    type="file"
                                    accept=".xlsx, .xls"
                                    onChange={handleFileChange}
                                    ref={fileInputRef}
                                ></Form.Control>
                            </div>
                        </Form.Group>
                    </Row>
                    <Row className="p-3">
                        <Col className="d-flex justify-content-start">
                            <Button
                                type="submit"
                                variant="success"
                                className="me-3 text-white rounded-0"
                                style={{ width: "100px" }}
                            >
                                Save
                            </Button>
                            <Button
                                type="reset"
                                variant="info"
                                className="me-3 text-white rounded-0"
                                style={{ width: "100px" }}
                            >
                                Reset
                            </Button>
                            <Link
                                to={"/vaccine/list"}
                                className={"btn btn-warning me-3 text-white rounded-0"}
                                style={{ width: "100px" }}
                            >
                                Cancel
                            </Link>
                        </Col>
                    </Row>
                </Form>
            </Container>
        </Container>
    )
}

export default ImportVaccine