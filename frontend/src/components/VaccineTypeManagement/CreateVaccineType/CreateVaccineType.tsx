import React, {ChangeEvent, useEffect, useRef, useState} from "react";
import {Button, Card, Col, Container, Form, FormCheck, FormControl, FormGroup, Row} from "react-bootstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import axios from "axios";
import {Link, useNavigate} from "react-router-dom";
import {zodResolver} from "@hookform/resolvers/zod";
import {z} from "zod";
import {SubmitHandler, useForm} from "react-hook-form";
import {faAddressBook, faImage} from "@fortawesome/free-solid-svg-icons";
import {Toast_Custom} from "../../Utils/Toast_Custom";
import {CreateVaccineTypeSchema} from "../../Interface/ValidationSchema/ValidationSchemas";
import LoadingOverlay from "../../Utils/LoadingOverlay/LoadingOverlay";
import BASE_URL from "../../Api/BaseApi";

type InputData = z.infer<typeof CreateVaccineTypeSchema>;

const CreateVaccineType = () => {
    const fileInputRef = useRef<HTMLInputElement>(null);
    const jsonFormData = require('json-form-data');
    const [isLoading, setIsLoading] = useState<boolean>(false);
    const nav = useNavigate();
    const {
        register, handleSubmit, reset,
        formState: {errors}, setValue
    } = useForm<InputData>({
        resolver: zodResolver(CreateVaccineTypeSchema),
    });
    document.title = "Create Vaccine Type";

    useEffect(() => {
        loadDefaultImage().then(() => console.log("Load default image success"))
    }, []);

    const loadDefaultImage = async () => {
        try {
            const response = await fetch(`${process.env.PUBLIC_URL}/assets/manager.png`);
            const blob = await response.blob();
            const file = new File([blob], "picture.png", {type: blob.type});
            setValue("file", file);
        } catch (error) {
            console.error("Error loading default image:", error);
        }
    };

    const handleReset = () => {
        reset();
        loadDefaultImage().then(() => console.log("Load default image success"));
    };

    const handleImageChange = (event: ChangeEvent<HTMLInputElement>) => {
        if (event.target.files && event.target.files[0]) {
            setValue('file', event.target.files[0]);
        }
    };

    const onSubmit: SubmitHandler<InputData> = async (data) => {
        setIsLoading(true);
        try {
            const parseData = CreateVaccineTypeSchema.parse({...data, status: true});
            const formDataToSend = jsonFormData({...parseData, status: true});
            const token = localStorage.getItem("token");
            await axios.request({
                headers: {
                    Authorization: `Bearer ${token}`,
                },
                method: "POST",
                url: `${BASE_URL}/vaccination/vaccinetype`,
                data: formDataToSend,
            })
                .then((response) => {
                    if (response.status === 200) {
                        Toast_Custom({
                            type: "success",
                            message: "Create vaccine type successfully",
                        })
                        nav("/vaccine_type/list");
                    }
                })
        } catch (error) {
            console.error("Error creating vaccine type:", error);
            if (axios.isAxiosError(error) && error.response) {
                if (error.response.data.message) {
                    Toast_Custom({
                        type: "error",
                        message: error.response.data.message
                    })
                }
            } else {
                Toast_Custom({
                    type: "error",
                    message: "An unknown error occurred"
                })
            }
        } finally {
            setTimeout(() => {
                setIsLoading(false);
            }, 500)
        }
    };

    return (
        <Container>
            {isLoading ? <LoadingOverlay/> : null}
            <Row className="mt-4 mb-2">
                <h4 style={{fontWeight: "bold"}}>CREATE VACCINE TYPE</h4>
            </Row>
            <Card>
                <Card.Body>
                    <Form onSubmit={handleSubmit(onSubmit)} onReset={handleReset}>
                        <Row className={"mb-4"}>
                            <Col>
                                <FormGroup className="text-start">
                                    <Form.Label htmlFor="id" className="fw-bold">
                                        Vaccine Type Code <span style={{color: "red"}}>(*):</span>
                                    </Form.Label>
                                    <div className="d-flex align-items-center">
                                        <FontAwesomeIcon icon={faAddressBook} className="me-3"/>
                                        <FormControl
                                            id="id"
                                            type="text"
                                            readOnly
                                            disabled
                                        ></FormControl>
                                    </div>
                                </FormGroup>
                            </Col>
                            <Col>
                                <FormGroup className="text-start">
                                    <Form.Label htmlFor="vaccineTypeName" className="fw-bold">
                                        Vaccine Type Name<span style={{color: "red"}}>(*):</span>
                                    </Form.Label>
                                    <div className="d-flex align-items-center">
                                        <FontAwesomeIcon icon={faAddressBook} className="me-3"/>
                                        <FormControl {...register("vaccineTypeName")}
                                                     id="vaccineTypeName"
                                                     type="text"
                                                     isInvalid={!!errors.vaccineTypeName}
                                        ></FormControl>
                                    </div>
                                    {errors.vaccineTypeName && (
                                        <p className="text-danger">{errors.vaccineTypeName.message}</p>
                                    )}
                                </FormGroup>
                            </Col>
                            <Col>
                                <FormGroup className="text-start">
                                    <Form.Label htmlFor="status" className="fw-bold">
                                        Active<span style={{color: "red"}}>(*):</span>
                                    </Form.Label>
                                    <div className="d-flex align-items-center">
                                        <FormCheck
                                            id="status"
                                            type="checkbox"
                                            label="Active"
                                            defaultChecked
                                            disabled
                                        >
                                        </FormCheck>
                                    </div>
                                    {errors.status && (
                                        <p className="text-danger">{errors.status.message}</p>
                                    )}
                                </FormGroup>
                            </Col>
                        </Row>
                        <Row className={"mb-4"}>
                            <Col>
                                <FormGroup className="text-start">
                                    <Form.Label htmlFor="description" className="fw-bold">
                                        Description:
                                    </Form.Label>
                                    <div className="d-flex align-items-center">
                                        <FontAwesomeIcon icon={faAddressBook} className="me-3"/>
                                        <FormControl
                                            as="textarea"
                                            id="description"
                                            type="text"
                                            {...register("description")}
                                            isInvalid={!!errors.description}
                                        ></FormControl>
                                    </div>
                                    {errors.description && (
                                        <p className="text-danger">{errors.description.message}</p>
                                    )}
                                </FormGroup>
                            </Col>
                        </Row>
                        <Row className={"mb-5"}>
                            <Row className="mb-4">
                                <Col md={5}>
                                    <FormGroup className="text-start">
                                        <Form.Label htmlFor="file" className="fw-bold">
                                            Vaccine Type Image:
                                        </Form.Label>
                                        <div className="d-flex align-items-center">
                                            <FontAwesomeIcon icon={faImage} className="me-3"/>
                                            <FormControl
                                                {...register("file")}
                                                id="file"
                                                type="file"
                                                onChange={handleImageChange}
                                                ref={fileInputRef}
                                            ></FormControl>
                                        </div>
                                    </FormGroup>
                                </Col>
                            </Row>
                        </Row>
                        <Row>
                            <Col className="d-flex justify-content-start">
                                <Button
                                    type="submit"
                                    variant="success"
                                    className="me-3 text-white rounded-0"
                                    style={{width: "100px"}}
                                >
                                    Save
                                </Button>
                                <Button
                                    type="reset"
                                    variant="info"
                                    className="me-3 text-white rounded-0"
                                    style={{width: "100px"}}
                                >
                                    Reset
                                </Button>
                                <Link
                                    to={"/vaccine_type/list"}
                                    className={"btn btn-warning me-3 text-white rounded-0"}
                                    style={{width: "100px"}}
                                >
                                    Cancel
                                </Link>
                            </Col>
                        </Row>
                    </Form>
                </Card.Body>
            </Card>

        </Container>
    )
}

export default CreateVaccineType