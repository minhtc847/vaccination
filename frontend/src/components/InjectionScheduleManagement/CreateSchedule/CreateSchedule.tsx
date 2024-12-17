import React, {useEffect, useState} from "react";
import {Button, Card, Col, Container, Form, FormControl, FormGroup, FormSelect, Row} from "react-bootstrap";
import axios from "axios";
import {Link, useNavigate} from "react-router-dom";
import {zodResolver} from "@hookform/resolvers/zod";
import {SubmitHandler, useForm} from "react-hook-form";
import {Toast_Custom} from "../../Utils/Toast_Custom";
import {CreateScheduleSchema} from "../../Interface/ValidationSchema/ValidationSchemas";
import LoadingOverlay from "../../Utils/LoadingOverlay/LoadingOverlay";
import items from "../../../data/location.json";
import {z} from "zod";
import BASE_URL from "../../Api/BaseApi";

type InputData = z.infer<typeof CreateScheduleSchema>;

type Vaccine = {
    id: number;
    vaccineName: string;
    status: boolean;
}

function CreateSchedule() {
    const [vaccine, setVaccine] = useState([]);
    const currentDate = new Date().toISOString().split("T")[0];
    const [isLoading, setIsLoading] = useState<boolean>(false);
    const nav = useNavigate();
    const blockInvalidChar = (e: React.KeyboardEvent<HTMLInputElement>) => ['e', 'E', '+', '-', '.'].includes(e.key) && e.preventDefault();
    document.title = "Create Injection Schedule";

    useEffect(() => {
        const fetchData = async () => {
            const token = localStorage.getItem("token");
            const response = await axios.request({
                headers: {
                    Authorization: `Bearer ${token}`,
                },
                method: "GET",
                url: `${BASE_URL}/vaccination/vaccine?pageSize=100`,
            })
            const data = response.data;
            setVaccine(data.content.filter((vaccine: Vaccine) => vaccine.status));
        };
        if (vaccine.length === 0) {
            fetchData();
        }
    }, [vaccine.length]);

    const {
        register,
        handleSubmit,
        reset,
        formState: {errors},
        watch
    } = useForm<InputData>({
        resolver: zodResolver(CreateScheduleSchema),
    });

    const handleReset = () => {
        reset();
    };

    const formatRequestData = (data: InputData) => {
        const {city, district, ward, streetAddress, ...restData} = data;
        const location = watch("city") + ", " + watch("district") + ", " + watch("ward") + ", " + watch("streetAddress");
        return {...restData, place: location};
    };

    const onSubmit: SubmitHandler<InputData> = async (data) => {
        setIsLoading(true);
        try {
            const token = localStorage.getItem("token");
            const requestData = formatRequestData(data);
            await axios.request({
                headers: {
                    Authorization: `Bearer ${token}`,
                },
                method: "POST",
                url: `${BASE_URL}/vaccination/schedule`,
                data: requestData,
            })
                .then((response) => {
                    if (response.status === 200) {
                        Toast_Custom({
                            type: "success",
                            message: "Create schedule successfully",
                        })
                        nav("/schedule/list")
                    }
                })
        } catch (error) {
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
                <h4 style={{fontWeight: "bold"}}>CREATE SCHEDULE</h4>
            </Row>
            <Card className="p-3">
                <Card.Body>
                    <Form onSubmit={handleSubmit(onSubmit)} onReset={handleReset}>
                        <Row className="mb-lg-4 mb-0">
                            <Col lg={4} className={"mb-lg-0 mb-4"}>
                                <FormGroup className="text-start">
                                    <Form.Label htmlFor="vaccineId" className="fw-bold">
                                        Vaccine ID <span style={{color: "red"}}>(*):</span>
                                    </Form.Label>
                                    <div className="d-flex align-items-center">
                                        <FormSelect
                                            {...register("vaccineId", {valueAsNumber: true})}
                                            id="vaccineId" isInvalid={!!errors.vaccineId}
                                        >
                                            <option value={0} defaultChecked> Select Vaccine</option>
                                            {Array.isArray(vaccine) && vaccine.map((v: Vaccine) => (
                                                <option key={v.id} value={v.id}>
                                                    {v.vaccineName!}
                                                </option>
                                            ))}
                                        </FormSelect>
                                    </div>
                                    {errors.vaccineId && (
                                        <p className="text-danger m-0">{errors.vaccineId.message}</p>
                                    )}
                                </FormGroup>
                            </Col>
                            <Col lg={4} sm={6} className={"mb-lg-0 mb-4"}>
                                <FormGroup className="text-start">
                                    <Form.Label htmlFor="startDate" className="fw-bold">
                                        From<span style={{color: "red"}}>(*):</span>
                                    </Form.Label>
                                    <div className="d-flex align-items-center">
                                        <FormControl {...register("startDate")}
                                                     id="startDate"
                                                     type="date"
                                                     min={currentDate}
                                                     isInvalid={!!errors.startDate}
                                        />
                                    </div>
                                    {errors.startDate && (
                                        <p className="text-danger m-0">{errors.startDate.message}</p>
                                    )}
                                </FormGroup>
                            </Col>
                            <Col lg={4} sm={6}>
                                <FormGroup className="text-start">
                                    <Form.Label htmlFor="endDate" className="fw-bold">
                                        To<span style={{color: "red"}}>(*):</span>
                                    </Form.Label>
                                    <div className="d-flex align-items-center">
                                        <FormControl {...register("endDate")}
                                                     id="endDate"
                                                     type="date"
                                                     min={currentDate}
                                                     isInvalid={!!errors.endDate}
                                        />
                                    </div>
                                    {errors.endDate && (
                                        <p className="text-danger m-0">{errors.endDate.message}</p>
                                    )}
                                </FormGroup>
                            </Col>
                        </Row>
                        <Row className="mb-4">
                            <Col>
                                <FormGroup className="text-start">
                                    <Form.Label className="fw-bold">
                                        Place<span style={{color: "red"}}>(*):</span>
                                    </Form.Label>
                                    <Row className="mb-0 mb-lg-4">
                                        <Col lg={4} className="mb-4 mb-lg-0">
                                            <div className="d-flex justify-content-center align-items-center">
                                                <Form.Label className="me-lg-2" htmlFor="city"
                                                            style={{margin: "0 50px 0 0"}}>
                                                    City:
                                                </Form.Label>
                                                <Form.Select id="city" {...register("city")} isInvalid={!!errors.city}>
                                                    <option value="">Select City</option>
                                                    {items?.map((c) => (
                                                        <option key={c.Code} value={c.FullName}>
                                                            {c.FullName}
                                                        </option>
                                                    ))}
                                                </Form.Select>
                                            </div>
                                            {errors.city &&
                                                <p className="text-danger m-0">{errors.city.message?.toString()}</p>}
                                        </Col>
                                        <Col lg={4} className="mb-4 mb-lg-0">
                                            <div className="d-flex justify-content-center align-items-center">
                                                <Form.Label className="me-lg-2" htmlFor="district"
                                                            style={{margin: "0 28px 0 0"}}>
                                                    District:
                                                </Form.Label>
                                                <Form.Select id="district" {...register("district")}
                                                             isInvalid={!!errors.district}>
                                                    <option value="">Select District</option>
                                                    {watch("city") !== '' && items?.find((item) => item.FullName === watch("city"))?.District.map((d) => (
                                                        <option key={d.Code} value={d.FullName}>
                                                            {d.FullName}
                                                        </option>
                                                    ))}
                                                </Form.Select>
                                            </div>
                                            {errors.district &&
                                                <p className="text-danger m-0">{errors.district.message?.toString()}</p>}
                                        </Col>
                                        <Col lg={4} className="mb-4 mb-lg-0">
                                            <div className="d-flex justify-content-center align-items-center">
                                                <Form.Label htmlFor="ward" className="me-lg-2"
                                                            style={{margin: "0 40px 0 0"}}>
                                                    Ward:
                                                </Form.Label>
                                                <Form.Select id="ward" {...register("ward")} isInvalid={!!errors.ward}>
                                                    <option value="">Select Ward</option>
                                                    {watch("district") !== '' &&
                                                        items?.find((item) => item.FullName === watch("city"))?.District
                                                            .find((d) => d.FullName === watch("district"))?.Ward.map((w) => (
                                                            <option key={w.Code} value={w.FullName}>
                                                                {w.FullName}
                                                            </option>
                                                        ))}
                                                </Form.Select>
                                            </div>
                                            {errors.ward &&
                                                <p className="text-danger m-0">{errors.ward.message?.toString()}</p>}
                                        </Col>
                                    </Row>
                                    <Row>
                                        <Col xs={12}>
                                            <FormGroup className="text-start">
                                                <Form.Label htmlFor="streetAddress">
                                                    Street address:
                                                </Form.Label>
                                                <div className="d-flex align-items-center">
                                                    <FormControl {...register("streetAddress")}
                                                                 id="streetAddress"
                                                                 type="text"
                                                                 isInvalid={!!errors.streetAddress}
                                                    />
                                                </div>
                                                {errors.streetAddress && (
                                                    <p className="text-danger m-0">{errors.streetAddress.message?.toString()}</p>
                                                )}
                                            </FormGroup>
                                        </Col>
                                    </Row>
                                </FormGroup>
                            </Col>
                        </Row>

                        <Row className="mb-4">
                            <Col>
                                <Row className="mb-4">
                                    <FormGroup className="text-start">
                                        <Form.Label htmlFor="injectPerDay" className="fw-bold">
                                            Inject Per Day<span style={{color: "red"}}>(*):</span>
                                        </Form.Label>
                                        <div className="d-flex align-items-center">
                                            <FormControl {...register("injectPerDay", {valueAsNumber: true})}
                                                         id="injectPerDay"
                                                         type="number"
                                                         onKeyDown={blockInvalidChar}
                                                         defaultValue={1}
                                                         min={1}
                                                         isInvalid={!!errors.injectPerDay}
                                            />
                                        </div>
                                        {errors.injectPerDay && (
                                            <p className="text-danger m-0">{errors.injectPerDay.message}</p>
                                        )}
                                    </FormGroup>
                                </Row>
                                <Row className="mb-4 mb-lg-0">
                                    <FormGroup className="text-start">
                                        <Form.Label htmlFor="injectionTimes" className="fw-bold">
                                            Injection Times<span style={{color: "red"}}>(*):</span>
                                        </Form.Label>
                                        <div className="d-flex align-items-center">
                                            <FormControl {...register("injectionTimes", {valueAsNumber: true})}
                                                         id="injectionTimes"
                                                         type="number"
                                                         onKeyDown={blockInvalidChar}
                                                         defaultValue={1}
                                                         min={1}
                                                         isInvalid={!!errors.injectionTimes}
                                            />
                                        </div>
                                        {errors.injectionTimes && (
                                            <p className="text-danger m-0">{errors.injectionTimes.message}</p>
                                        )}
                                    </FormGroup>
                                </Row>
                            </Col>
                            <Col lg={8}>
                                <FormGroup className="text-start">
                                    <Form.Label htmlFor="description" className="fw-bold">
                                        Note:
                                    </Form.Label>
                                    <div className="d-flex align-items-center">
                                        <FormControl {...register("description")}
                                                     id="description"
                                                     type="text"
                                                     as="textarea"
                                                     isInvalid={!!errors.description}
                                                     rows={!!errors.injectionTimes ? 6 : 5}
                                        />
                                    </div>
                                    {errors.description && (
                                        <p className="text-danger m-0">{errors.description.message}</p>
                                    )}
                                </FormGroup>
                            </Col>
                        </Row>
                        <Row className="mb-4 justify-content-lg-start justify-content-center">
                            <Col xs={"auto"}>
                                <Button
                                    type="submit"
                                    variant="success"
                                    className="text-white px-4">
                                    Save
                                </Button>
                            </Col>
                            <Col xs={"auto"}>
                                <Button
                                    type="reset"
                                    variant="info"
                                    className="text-white px-4">
                                    Reset
                                </Button>
                            </Col>
                            <Col xs={"auto"}>
                                <Link to={"/schedule/list"} id="cancelBtn" hidden></Link>
                                <Button type="button" variant="warning" className="text-white px-4"
                                        onClick={() => document.getElementById("cancelBtn")?.click()}>
                                    Cancel
                                </Button>
                            </Col>
                        </Row>
                    </Form>
                </Card.Body>
            </Card>
        </Container>
    )
}

export default CreateSchedule;