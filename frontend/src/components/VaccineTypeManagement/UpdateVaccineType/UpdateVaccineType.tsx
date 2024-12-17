import React, {ChangeEvent, useEffect, useRef, useState} from "react";
import {Button, Card, Col, Container, Form, FormCheck, FormControl, FormGroup, Row} from "react-bootstrap";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import axios from "axios";
import {Link, useNavigate, useParams} from "react-router-dom";
import {zodResolver} from "@hookform/resolvers/zod";
import {z} from "zod";
import {SubmitHandler, useForm} from "react-hook-form";
import {faAddressBook, faImage} from "@fortawesome/free-solid-svg-icons";
import {Toast_Custom} from "../../Utils/Toast_Custom";
import {UpdateVaccineTypeSchema} from "../../Interface/ValidationSchema/ValidationSchemas";
import LoadingOverlay from "../../Utils/LoadingOverlay/LoadingOverlay";
import BASE_URL from "../../Api/BaseApi";

type InputData = z.infer<typeof UpdateVaccineTypeSchema>;

const UpdateVaccineType = () => {
    const {id} = useParams<{ id: string }>();
    const fileInputRef = useRef<HTMLInputElement>(null);
    const jsonFormData = require('json-form-data');
    const [render, setRender] = useState(0);
    const [isLoading, setIsLoading] = useState<boolean>(false);
    const [fileName, setFileName] = useState<string>('');
    const [fileError, setFileError] = useState<string>('');
    const nav = useNavigate();

    const {
        register, handleSubmit, reset, watch,
        formState: {errors}, setValue
    } = useForm<InputData>({
        resolver: zodResolver(UpdateVaccineTypeSchema),
    });
    document.title = "Update Vaccine Type";

    useEffect(() => {
        const fetchData = async () => {
            if (id) {
                try {
                    const token = localStorage.getItem("token");
                    const response = await axios.get<InputData>(`${BASE_URL}/vaccination/vaccinetype/${id}`, {
                        headers: {Authorization: `Bearer ${token}`},
                    });
                    const data = response.data;
                    reset(data);
                    setValue("status", (data.status));
                } catch (error) {
                    console.error("Error fetching vaccine type data:", error);
                }
            }
        };
        fetchData();
    }, [id, reset, render, setValue]);

    const handleImageChange: (event: ChangeEvent<HTMLInputElement>) => void = (event) => {
        if (event.target.files && event.target.files[0]) {
            const file: File = event.target.files[0];
            const allowedTypes: string[] = ['image/png', 'image/jpeg', 'image/gif', 'image/bmp'];
            const maxSize: number = 2 * 1024 * 1024; // 2MB

            if (!allowedTypes.includes(file.type)) {
                setFileError('Invalid file type. Only .png, .jpg, .jpeg, .gif, .bmp are allowed.');
                setFileName('');
                setValue('file', undefined);
            } else if (file.size > maxSize) {
                setFileError('File size exceeds 2MB limit.');
                setFileName('');
                setValue('file', undefined);
            } else {
                setFileError('');
                setFileName(file.name);
                setValue('file', file);
            }
        } else {
            setFileName('');
            setFileError('');
            setValue('file', undefined);
        }
    };

    const handleReset = () => {
        setRender(render + 1);
    };

    const onSubmit: SubmitHandler<InputData> = async (data) => {
        setIsLoading(true);
        try {
            const formDataToSend = jsonFormData({...data});
            const token = localStorage.getItem("token");
            await axios.request({
                headers: {
                    Authorization: `Bearer ${token}`,
                },
                method: "PUT",
                url: `${BASE_URL}/vaccination/vaccinetype/${id}`,
                data: formDataToSend,
            })
                .then((response) => {
                    if (response.status === 200) {
                        Toast_Custom({
                            type: "success",
                            message: "Update vaccine type successfully",
                        })
                        nav("/vaccine_type/list")
                    }
                })
        } catch (error) {
            console.error("Error updating vaccine type:", error);
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
                <h4 style={{fontWeight: "bold"}}>UPDATE VACCINE TYPE</h4>
            </Row>
            <Card>
                <Card.Body>
                    <Form onSubmit={handleSubmit(onSubmit)} onReset={handleReset}>
                        <Row>
                            <Col>
                                <FormGroup className="text-start">
                                    <Form.Label className="fw-bold">
                                        Vaccine Type Code <span style={{color: "red"}}>(*):</span>
                                    </Form.Label>
                                    <div className="d-flex align-items-center">
                                        <FontAwesomeIcon icon={faAddressBook} className="me-3"/>
                                        <FormControl
                                            type="text"
                                            value={watch("code")}
                                            readOnly
                                            disabled
                                        />
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
                                                     value={watch("vaccineTypeName")}
                                                     isInvalid={!!errors.vaccineTypeName}
                                        />
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
                                        <FontAwesomeIcon icon={faAddressBook} className="me-3"/>
                                        <FormCheck
                                            id="status"
                                            type="checkbox"
                                            label="Active"
                                            defaultChecked={watch("status")}
                                            disabled
                                        />
                                    </div>
                                </FormGroup>
                            </Col>
                        </Row>
                        <Row className="mb-4">
                            <Col>
                                <FormGroup className="text-start">
                                    <Form.Label htmlFor="description" className="fw-bold">
                                        Description<span style={{color: "red"}}>(*):</span>
                                    </Form.Label>
                                    <div className="d-flex align-items-center">
                                        <FontAwesomeIcon icon={faAddressBook} className="me-3"/>
                                        <FormControl
                                            as="textarea"
                                            id="description"
                                            {...register("description")}
                                            value={watch("description")}
                                            isInvalid={!!errors.description}
                                        />
                                    </div>
                                    {errors.description && (
                                        <p className="text-danger">{errors.description.message}</p>
                                    )}
                                </FormGroup>
                            </Col>
                        </Row>
                        <Row className="mb-4">
                            <Col lg={6}>
                                <FormGroup className="text-start">
                                    <Form.Label htmlFor="file" className="fw-bold">
                                        Vaccine Type Image:
                                    </Form.Label>
                                    <div className="d-flex align-items-center">
                                        <FontAwesomeIcon icon={faImage} className="me-3 mb-4"/>
                                        <div>
                                            <FormControl
                                                {...register("file")}
                                                id="file"
                                                type="file"
                                                onChange={handleImageChange}
                                                ref={fileInputRef}
                                                accept=".png, .jpg, .jpeg, .gif, .bmp"
                                            ></FormControl>
                                        </div>
                                    </div>
                                </FormGroup>
                            </Col>
                        </Row>
                        <Row className="pt-5">
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

export default UpdateVaccineType;