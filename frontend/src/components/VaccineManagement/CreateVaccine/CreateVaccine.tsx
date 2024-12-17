import React, {useEffect, useState} from "react";
import {Button, Card, Col, Container, Form, FormCheck, FormControl, FormGroup, FormSelect, Row} from "react-bootstrap";
import axios from "axios";
import {Link, useNavigate} from "react-router-dom";
import {zodResolver} from "@hookform/resolvers/zod";
import {z} from "zod";
import {SubmitHandler, useFieldArray, useForm} from "react-hook-form";
import {Toast_Custom} from "../../Utils/Toast_Custom";
import {CreateVaccineSchema} from "../../Interface/ValidationSchema/ValidationSchemas";
import LoadingOverlay from "../../Utils/LoadingOverlay/LoadingOverlay";
import BASE_URL from "../../Api/BaseApi";

type InputData = z.infer<typeof CreateVaccineSchema>;

type vaccineType = {
    id: number;
    vaccineTypeName: string;
    status: boolean;
    description: string;
}

function CreateVaccine() {
    // State for storing vaccine types
    const [vaccineTypes, setVaccineTypes] = useState([]);

    // State for loading indicator
    const [isLoading, setIsLoading] = useState<boolean>(false);

    // Navigation hook
    const nav = useNavigate();

    // Function to block invalid characters
    const blockInvalidChar = (e: React.KeyboardEvent<HTMLInputElement>) => ['e', 'E', '+', '-', '.'].includes(e.key) && e.preventDefault();

    // Form handling using react-hook-form
    const {
        register,
        handleSubmit,
        reset,
        formState: {errors},
        control
    } = useForm<InputData>({
        resolver: zodResolver(CreateVaccineSchema),
    });

    const {fields, append, remove} = useFieldArray({
        control,
        name: "contraindication" as never,
    });
    document.title = "Create Vaccine";

    // Fetch data from the API if vaccineTypes is empty
    useEffect(() => {
        /**
         * Fetch data from the API to populate vaccine types
         */
        const fetchData = async () => {
            const token = localStorage.getItem("token");
            const api = `${BASE_URL}/vaccination/vaccinetype?pageSize=1000`;
            const response = await axios.request({
                headers: {
                    Authorization: `Bearer ${token}`,
                },
                method: "GET",
                url: api,
            })
            const data = response.data;
            setVaccineTypes(data.content.filter((type: vaccineType) => type.status));
        };

        // Call fetchData if vaccineTypes is empty
        if (vaccineTypes.length === 0) {
            fetchData().then();
        }
    }, [vaccineTypes.length]);

    /**
     * Reset the form fields
     */
    const handleReset = () => {
        reset();
    };

    const onSubmit: SubmitHandler<InputData> = async (data) => {
        setIsLoading(true);
        try {
            const parseData = CreateVaccineSchema.parse(data);
            const token = localStorage.getItem("token");
            await axios.request({
                headers: {
                    Authorization: `Bearer ${token}`,
                },
                method: "POST",
                url: `${BASE_URL}/vaccination/vaccine`,
                data: {...parseData, status: true}
            })
                .then((response) => {
                    if (response.status === 200) {
                        Toast_Custom({
                            type: "success",
                            message: "Create vaccine successfully",
                        })
                        nav("/vaccine/list")
                    }
                })
        } catch
            (error) {
            console.error("Error creating vaccine:", error);
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
    }

    return (
        <Container>
            {isLoading ? <LoadingOverlay/> : null}
            <Row className="mt-4 mb-2">
                <h4 style={{fontWeight: "bold"}}>CREATE VACCINE</h4>
            </Row>
            <Card>
                <Card.Body>
                    <Form onSubmit={handleSubmit(onSubmit)} onReset={handleReset}>
                        <Row className="mb-4">
                            <Col md={8}>
                                <FormGroup className="text-start">
                                    <Form.Label className="fw-bold">
                                        Vaccine ID <span style={{color: "red"}}>(*):</span>
                                    </Form.Label>
                                    <div className="d-flex align-items-center">
                                        <FormControl
                                            type="text"
                                            readOnly
                                            disabled
                                        ></FormControl>
                                    </div>
                                </FormGroup>
                            </Col>
                            <Col>
                                <FormGroup className="text-start">
                                    <Form.Label htmlFor="status" className="fw-bold mb-3">
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
                                </FormGroup>
                            </Col>
                        </Row>
                        <Row className="mb-4">
                            <Col>
                                <FormGroup className="text-start">
                                    <Form.Label htmlFor="vaccineName" className="fw-bold">
                                        Vaccine Name<span style={{color: "red"}}>(*):</span>
                                    </Form.Label>
                                    <div className="d-flex align-items-center">
                                        <FormControl {...register("vaccineName")}
                                                     id="vaccineName"
                                                     type="text"
                                                     isInvalid={!!errors.vaccineName}
                                        />
                                    </div>
                                    {errors.vaccineName && (
                                        <p className="text-danger">{errors.vaccineName.message}</p>
                                    )}
                                </FormGroup>
                            </Col>
                            <Col>
                                <FormGroup className="text-start">
                                    <Form.Label htmlFor="vaccineTypeId" className="fw-bold">
                                        Vaccine Type<span style={{color: "red"}}>(*):</span>
                                    </Form.Label>
                                    <div className="d-flex align-items-center">
                                        <FormSelect
                                            {...register("vaccineTypeId", {valueAsNumber: true})}
                                            id="vaccineTypeId"
                                            isInvalid={!!errors.vaccineTypeId}
                                        >
                                            <option defaultChecked> Select vaccine type</option>
                                            {Array.isArray(vaccineTypes) && vaccineTypes.map((t: vaccineType) => (
                                                <option key={t.id} value={t.id}>
                                                    {t.vaccineTypeName!}
                                                </option>
                                            ))}
                                        </FormSelect>
                                    </div>
                                    {errors.vaccineTypeId && (
                                        <p className="text-danger">{errors.vaccineTypeId.message}</p>
                                    )}
                                </FormGroup>
                            </Col>
                            <Col>
                                <FormGroup className="text-start">
                                    <Form.Label htmlFor="numberOfInjection" className="fw-bold">
                                        Number of Inject:
                                    </Form.Label>
                                    <div className="d-flex align-items-center">
                                        <FormControl {...register("numberOfInjection", {valueAsNumber: true})}
                                                     id="numberOfInjection"
                                                     type="number"
                                                     min={1}
                                                     onKeyDown={blockInvalidChar}
                                                     isInvalid={!!errors.numberOfInjection}
                                        />
                                    </div>
                                    {errors.numberOfInjection && (
                                        <p className="text-danger">{errors.numberOfInjection.message}</p>
                                    )}
                                </FormGroup>
                            </Col>
                        </Row>
                        <Row className="mb-4">
                            <Col>
                                <FormGroup className="text-start">
                                    <Form.Label htmlFor="usage" className="fw-bold">
                                        Usage:
                                    </Form.Label>
                                    <div className="d-flex align-items-center">
                                        <FormControl {...register("usage")}
                                                     id="usage"
                                                     type="text"
                                                     as="textarea"
                                                     rows={4}
                                                     isInvalid={!!errors.usage}
                                        />
                                    </div>
                                    {errors.usage && (
                                        <p className="text-danger">{errors.usage.message}</p>
                                    )}
                                </FormGroup>
                            </Col>
                            <Col>
                                <FormGroup className="text-start">
                                    <Form.Label htmlFor="indication" className="fw-bold">
                                        Indication:
                                    </Form.Label>
                                    <div className="d-flex align-items-center">
                                        <FormControl {...register("indication")}
                                                     id="indication"
                                                     type="text"
                                                     as="textarea"
                                                     rows={4}
                                                     isInvalid={!!errors.indication}
                                        />
                                    </div>
                                    {errors.indication && (
                                        <p className="text-danger">{errors.indication.message}</p>
                                    )}
                                </FormGroup>
                            </Col>
                        </Row>
                        <Row className="mb-4">
                            <Col>
                                <p className="fw-bold text-start">
                                    Contraindication:
                                </p>
                                {fields.map((field, index) => (
                                    <Row key={field.id} className="mb-3 align-items-start">
                                        <Col md={"auto"} className="text-start pe-0 mt-1">
                                            <Form.Label htmlFor={`contraindication.${index}`}>
                                                Contraindication #{index + 1}:
                                            </Form.Label>
                                        </Col>
                                        <Col md={4} className="text-start">
                                            <div className="d-flex align-items-center ">
                                                <FormControl
                                                    id={`contraindication.${index}`}
                                                    {...register(`contraindication.${index}`)}
                                                    placeholder="Enter contraindication..."
                                                    isInvalid={!!errors.contraindication?.[index]}
                                                />
                                                <Button onClick={() => remove(index)}
                                                        className="ms-2 btn-close">
                                                </Button>
                                            </div>
                                            {errors.contraindication && errors.contraindication[index] && (
                                                <Form.Control.Feedback type="invalid" className="d-block">
                                                    {errors?.contraindication[index]?.message}
                                                </Form.Control.Feedback>
                                            )}
                                        </Col>
                                    </Row>
                                ))}
                                <Row>
                                    <Col className={"text-start"}>
                                        <Button type="button" variant="light" onClick={() => append('')}
                                                className="border border-2 rounded-pill text-secondary">
                                            + Add Contraindication
                                        </Button>
                                    </Col>
                                </Row>
                            </Col>
                        </Row>
                        <Row className="mb-4">
                            <Col>
                                <FormGroup className="text-start">
                                    <Form.Label htmlFor="timeBeginNextInjection" className="fw-bold">
                                        Time of beginning next injection:
                                    </Form.Label>
                                    <div className="d-flex align-items-center">
                                        <FormControl {...register("timeBeginNextInjection", {valueAsNumber: true})}
                                                     id="timeBeginNextInjection"
                                                     type="number"
                                                     min={1}
                                                     onKeyDown={blockInvalidChar}
                                                     isInvalid={!!errors.timeBeginNextInjection}
                                        />
                                    </div>
                                    {errors.timeBeginNextInjection && (
                                        <p className="text-danger">{errors.timeBeginNextInjection.message}</p>
                                    )}
                                </FormGroup>
                            </Col>
                            <Col>
                                <FormGroup className="text-start">
                                    <Form.Label htmlFor="totalInject" className="fw-bold">
                                        Total Inject:
                                    </Form.Label>
                                    <div className="d-flex align-items-center">
                                        <FormControl {...register("totalInject", {valueAsNumber: true})}
                                                     id="totalInject"
                                                     type="number"
                                                     min={1}
                                                     onKeyDown={blockInvalidChar}
                                                     isInvalid={!!errors.totalInject}
                                        />
                                    </div>
                                    {errors.totalInject && (
                                        <p className="text-danger">{errors.totalInject.message}</p>
                                    )}
                                </FormGroup>
                            </Col>
                            <Col>
                                <FormGroup className="text-start">
                                    <Form.Label htmlFor="origin" className="fw-bold">
                                        Origin:
                                    </Form.Label>
                                    <div className="d-flex align-items-center">
                                        <FormControl {...register("origin")}
                                                     id="origin"
                                                     type="text"
                                                     isInvalid={!!errors.origin}
                                        />
                                    </div>
                                    {errors.origin && (
                                        <p className="text-danger">{errors.origin.message}</p>
                                    )}
                                </FormGroup>
                            </Col>
                        </Row>
                        <Row className="mb-4">
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
                                    to={"/vaccine/list"}
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

export default CreateVaccine